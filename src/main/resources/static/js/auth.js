(function () {
    const TOKEN_KEY = 'bloodDonationToken';
    const USER_KEY = 'bloodDonationUser';
    const originalFetch = window.fetch.bind(window);

    const pageAccess = {
        'activity.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'appointment.html': ['ROLE_DONOR'],
        'collection.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'donor.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'stock.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']
    };

    const navAccess = {
        'activity.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'appointment.html': ['ROLE_DONOR'],
        'collection.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'donor.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
        'stock.html': ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']
    };

    function currentPage() {
        const page = window.location.pathname.split('/').pop();
        return page || 'index.html';
    }

    function isLoginPage() {
        return currentPage() === 'login.html';
    }

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        const raw = localStorage.getItem(USER_KEY);
        if (!raw) {
            return null;
        }
        try {
            return JSON.parse(raw);
        } catch (error) {
            return null;
        }
    }

    function getUserRole() {
        const user = getUser();
        return user ? user.role : null;
    }

    function hasAnyRole(roles) {
        const role = getUserRole();
        return roles.includes(role);
    }

    function homePage(role) {
        return role === 'ROLE_DONOR' ? 'appointment.html' : 'index.html';
    }

    function safeRedirectTarget(target, role) {
        if (!target || target.includes(':') || target.startsWith('/') || target.startsWith('\\')) {
            return homePage(role);
        }
        return target;
    }

    function loginUrl() {
        const target = currentPage() + window.location.search + window.location.hash;
        return 'login.html?redirect=' + encodeURIComponent(target);
    }

    function redirectToLogin() {
        if (!isLoginPage()) {
            window.location.replace(loginUrl());
        }
    }

    function logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        window.location.replace('login.html');
    }

    function saveSession(loginData) {
        localStorage.setItem(TOKEN_KEY, loginData.token);
        localStorage.setItem(USER_KEY, JSON.stringify({
            userId: loginData.userId,
            username: loginData.username,
            realName: loginData.realName,
            role: loginData.role
        }));
    }

    async function login(username, password) {
        const response = await originalFetch('/api/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const result = await response.json();
        if (result.code !== 200 || !result.data || !result.data.token) {
            throw new Error(result.msg || '登录失败');
        }
        saveSession(result.data);
        return result.data;
    }

    function addAuthHeader(input, init) {
        const token = getToken();
        if (!token) {
            return init;
        }
        const headers = new Headers(init.headers || {});
        if (!headers.has('Authorization')) {
            headers.set('Authorization', 'Bearer ' + token);
        }
        return Object.assign({}, init, { headers });
    }

    function isApiRequest(input) {
        const url = typeof input === 'string' ? input : input.url;
        return url.startsWith('/api') || url.startsWith(window.location.origin + '/api');
    }

    window.fetch = async function (input, init) {
        const requestInit = init || {};
        const nextInit = isApiRequest(input) ? addAuthHeader(input, requestInit) : requestInit;
        const response = await originalFetch(input, nextInit);

        if (isApiRequest(input) && response.status === 401) {
            logout();
            return response;
        }

        if (isApiRequest(input) && response.status === 403) {
            alert('权限不足，无法执行该操作');
        }

        return response;
    };

    function injectAuthStyle() {
        const style = document.createElement('style');
        style.textContent = `
            .nav {
                display: flex;
                justify-content: center;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
            }
            .nav a {
                margin: 0 4px !important;
            }
            .auth-user {
                color: #fff;
                font-size: 14px;
                margin-left: 16px;
                opacity: 0.95;
            }
            .auth-logout {
                background: transparent;
                color: #fff;
                border: 1px solid rgba(255,255,255,0.55);
                padding: 6px 12px;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
            }
            .auth-logout:hover {
                background: rgba(255,255,255,0.14);
            }
        `;
        document.head.appendChild(style);
    }

    function applyNavAccess() {
        const role = getUserRole();
        document.querySelectorAll('.nav a').forEach(link => {
            const target = link.getAttribute('href');
            const roles = navAccess[target];
            if (roles && !roles.includes(role)) {
                link.style.display = 'none';
            }
        });
    }

    function injectUserBar() {
        const nav = document.querySelector('.nav');
        const user = getUser();
        if (!nav || !user) {
            return;
        }

        const name = document.createElement('span');
        name.className = 'auth-user';
        name.textContent = user.realName || user.username;

        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'auth-logout';
        button.textContent = '退出';
        button.addEventListener('click', logout);

        nav.appendChild(name);
        nav.appendChild(button);
    }

    function enforcePageAccess() {
        const roles = pageAccess[currentPage()];
        if (roles && !hasAnyRole(roles)) {
            alert('权限不足，无法访问该页面');
            window.location.replace(homePage(getUserRole()));
        }
    }

    window.BloodAuth = {
        login,
        logout,
        getToken,
        getUser,
        getUserRole,
        hasAnyRole,
        homePage,
        safeRedirectTarget
    };

    if (isLoginPage()) {
        const role = getUserRole();
        if (getToken() && role) {
            window.location.replace(homePage(role));
        }
        return;
    }

    if (!getToken()) {
        redirectToLogin();
        return;
    }

    document.addEventListener('DOMContentLoaded', () => {
        injectAuthStyle();
        enforcePageAccess();
        applyNavAccess();
        injectUserBar();
    });
})();
