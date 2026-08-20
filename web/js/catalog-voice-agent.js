(function () {
    const agentUrl = AppConfig.voiceAgentUrl;
    const sessionId = `mall-home-${Date.now()}-${Math.random().toString(16).slice(2)}`;
    let registered = null;

    const button = document.createElement('button');
    button.className = 'voice-agent-fab';
    button.type = 'button';
    button.textContent = '🎤 语音找好物';

    const overlay = document.createElement('div');
    overlay.className = 'voice-agent-overlay';
    overlay.innerHTML = `
        <div class="voice-agent-panel">
            <button class="voice-agent-close" type="button" aria-label="关闭">×</button>
            <iframe class="voice-agent-frame" title="爱拼团语音导购" allow="microphone"></iframe>
        </div>`;
    document.body.append(button, overlay);
    const frame = overlay.querySelector('.voice-agent-frame');

    async function waitForContext() {
        if (window.MallCatalogContext) return window.MallCatalogContext;
        return new Promise((resolve, reject) => {
            const timer = setTimeout(() => reject(new Error('商品目录加载超时，请刷新页面')), 10000);
            window.addEventListener('mall-catalog-context-ready', event => {
                clearTimeout(timer);
                resolve(event.detail);
            }, { once: true });
        });
    }

    async function register() {
        if (registered) return registered;
        const context = await waitForContext();
        button.disabled = true;
        button.textContent = '🎤 正在连接全部商品…';
        const response = await fetch(agentUrl + '/api/v1/integration/mall/catalog-session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId, mallUserId: context.mallUserId, products: context.products })
        });
        if (!response.ok) throw new Error(`语音 Agent 连接失败（HTTP ${response.status}）`);
        registered = await response.json();
        frame.src = agentUrl + '/voice-test.html?mall=1&home=1&userId='
            + encodeURIComponent(registered.agentUserId) + '&sessionId=' + encodeURIComponent(sessionId);
        button.disabled = false;
        button.textContent = '🎤 语音找好物';
        return registered;
    }

    async function openVoice() {
        try {
            await register();
            overlay.classList.add('open');
        } catch (error) {
            button.disabled = false;
            button.textContent = '🎤 语音找好物';
            alert(error.message || '语音导购暂时不可用');
        }
    }

    button.addEventListener('click', openVoice);
    document.getElementById('heroVoiceButton').addEventListener('click', openVoice);
    overlay.querySelector('.voice-agent-close').addEventListener('click', () => overlay.classList.remove('open'));
    overlay.addEventListener('click', event => {
        if (event.target === overlay) overlay.classList.remove('open');
    });

    window.addEventListener('message', event => {
        if (event.origin !== new URL(agentUrl).origin) return;
        if (!event.data || event.data.type !== 'mall-voice-action') return;
        const payload = event.data.payload || {};
        if (!payload.goodsId) return;
        const action = payload.action === 'group-buy' ? '&autoAction=group-buy' : '';
        location.href = `index.html?goodsId=${encodeURIComponent(payload.goodsId)}${action}`;
    });
})();
