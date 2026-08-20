(function () {
    const state = { keyword: '', category: '推荐', maxPrice: '', products: [] };
    const grid = document.getElementById('productGrid');
    const empty = document.getElementById('emptyState');
    const title = document.getElementById('resultTitle');
    const hint = document.getElementById('resultHint');
    const count = document.getElementById('resultCount');
    const input = document.getElementById('searchInput');

    function escapeHtml(value) {
        return String(value == null ? '' : value).replace(/[&<>'"]/g, char => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
        })[char]);
    }

    function catalogUrl() {
        const params = new URLSearchParams();
        if (state.keyword) params.set('keyword', state.keyword);
        if (state.category && state.category !== '推荐') params.set('category', state.category);
        if (state.maxPrice) params.set('maxPrice', state.maxPrice);
        return AppConfig.sPayMallUrl + '/api/v1/catalog/products?' + params.toString();
    }

    async function loadProducts() {
        grid.innerHTML = '<div class="loading-card"></div>'.repeat(4);
        empty.hidden = true;
        try {
            const response = await fetch(catalogUrl());
            const payload = await response.json();
            if (payload.code !== '0000') throw new Error(payload.info || '商品目录加载失败');
            state.products = payload.data || [];
            renderProducts();
        } catch (error) {
            grid.innerHTML = '';
            empty.hidden = false;
            empty.querySelector('h3').textContent = '商品目录暂时不可用';
            empty.querySelector('p').textContent = error.message;
        }
    }

    function renderProducts() {
        const products = state.products;
        grid.innerHTML = '';
        empty.hidden = products.length !== 0;
        count.textContent = products.length ? `共 ${products.length} 件商品` : '';
        title.textContent = state.keyword ? `“${state.keyword}”的搜索结果`
            : state.category === '推荐' ? '今日推荐' : state.category + '好物';
        hint.textContent = state.maxPrice ? `已筛选拼团价 ¥${state.maxPrice} 以内`
            : '正在为你挑选高性价比拼团好物';

        products.forEach(product => {
            const card = document.createElement('article');
            card.className = 'product-card';
            card.tabIndex = 0;
            card.innerHTML = `
                <div class="product-image">
                    <img src="${escapeHtml(product.imageUrl)}" alt="${escapeHtml(product.productName)}" loading="lazy">
                    <span class="product-badge">${escapeHtml(product.badge || '限时拼团')}</span>
                </div>
                <div class="product-body">
                    <span class="product-category">${escapeHtml(product.category)}</span>
                    <h3 class="product-name">${escapeHtml(product.productName)}</h3>
                    <p class="product-desc">${escapeHtml(product.productDesc)}</p>
                    <div class="product-meta">
                        <div class="price-row"><span class="group-price"><small>¥</small>${Number(product.groupPrice).toFixed(0)}</span><span class="original-price">¥${Number(product.originalPrice).toFixed(0)}</span></div>
                        <span class="participants">${Number(product.participantCount || 0).toLocaleString()}人已参团</span>
                    </div>
                    <button class="join-button" type="button">查看详情 · 立即拼团</button>
                </div>`;
            const open = () => location.href = `index.html?goodsId=${encodeURIComponent(product.productId)}`;
            card.addEventListener('click', open);
            card.addEventListener('keydown', event => {
                if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); open(); }
            });
            grid.appendChild(card);
        });
    }

    document.getElementById('searchForm').addEventListener('submit', event => {
        event.preventDefault();
        state.keyword = input.value.trim();
        loadProducts();
    });

    document.getElementById('categoryNav').addEventListener('click', event => {
        const button = event.target.closest('button[data-category]');
        if (!button) return;
        document.querySelectorAll('#categoryNav button').forEach(item => item.classList.toggle('active', item === button));
        state.category = button.dataset.category;
        state.keyword = '';
        input.value = '';
        loadProducts();
    });

    document.getElementById('priceFilter').addEventListener('click', event => {
        const button = event.target.closest('button[data-price]');
        if (!button) return;
        document.querySelectorAll('#priceFilter button').forEach(item => item.classList.toggle('active', item === button));
        state.maxPrice = button.dataset.price;
        loadProducts();
    });

    async function bootstrapCatalog() {
        const userId = AppUtils.getCurrentUserId();
        if (!userId) return;
        await loadProducts();
        try {
            const response = await fetch(AppConfig.sPayMallUrl + '/api/v1/catalog/products');
            const payload = await response.json();
            if (payload.code === '0000') {
                window.MallCatalogContext = { mallUserId: userId, products: payload.data || [] };
                window.dispatchEvent(new CustomEvent('mall-catalog-context-ready', { detail: window.MallCatalogContext }));
            }
        } catch (error) {
            console.warn('语音导购商品上下文加载失败', error);
        }
    }

    bootstrapCatalog();
})();
