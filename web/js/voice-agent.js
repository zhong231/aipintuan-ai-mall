(function () {
    const agentUrl = AppConfig.voiceAgentUrl;
    const sessionId = `mall-voice-${Date.now()}-${Math.random().toString(16).slice(2)}`;
    let registered = null;

    const button = document.createElement('button');
    button.className = 'voice-agent-fab';
    button.type = 'button';
    button.textContent = '🎤 语音导购';

    const overlay = document.createElement('div');
    overlay.className = 'voice-agent-overlay';
    overlay.innerHTML = `
        <div class="voice-agent-panel">
            <button class="voice-agent-close" type="button" aria-label="关闭">×</button>
            <iframe class="voice-agent-frame" title="语音导购" allow="microphone"></iframe>
        </div>`;

    document.body.append(button, overlay);
    const frame = overlay.querySelector('.voice-agent-frame');

    function context() {
        return window.MallVoiceContext;
    }

    async function register() {
        if (registered) return registered;
        if (!context()) throw new Error('商城商品信息还没有加载完成');

        button.disabled = true;
        button.textContent = '🎤 导购连接中…';
        const response = await fetch(agentUrl + '/api/v1/integration/mall/session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId, ...context() })
        });
        if (!response.ok) throw new Error(`语音 Agent 连接失败（HTTP ${response.status}）`);
        registered = await response.json();
        frame.src = agentUrl + '/voice-test.html?mall=1&userId='
            + encodeURIComponent(registered.agentUserId) + '&sessionId=' + encodeURIComponent(sessionId);
        button.disabled = false;
        button.textContent = '🎤 语音导购';
        return registered;
    }

    button.addEventListener('click', async () => {
        try {
            await register();
            overlay.classList.add('open');
        } catch (error) {
            button.disabled = false;
            button.textContent = '🎤 语音导购';
            alert(error.message || '语音导购暂时不可用');
        }
    });

    overlay.querySelector('.voice-agent-close').addEventListener('click', () => {
        overlay.classList.remove('open');
    });
    overlay.addEventListener('click', event => {
        if (event.target === overlay) overlay.classList.remove('open');
    });

    window.addEventListener('message', event => {
        if (![agentUrl, agentUrl.replace('127.0.0.1', 'localhost')].includes(event.origin)) return;
        if (!event.data || event.data.type !== 'mall-voice-action') return;

        const payload = event.data.payload || {};
        const expectedGoodsId = String(context() && context().goodsId || '');
        if (payload.goodsId && String(payload.goodsId) !== expectedGoodsId) {
            console.warn('忽略了不属于当前商品页的语音购买动作', payload);
            return;
        }

        const selector = payload.action === 'group-buy' ? '.group-buy' : '.buy-alone';
        const target = document.querySelector(selector);
        if (!target) return alert('商城购买按钮尚未准备好，请稍后重试');
        overlay.classList.remove('open');
        target.click();
    });
})();
