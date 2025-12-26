import store from '@/store';
let ws = null;

export function connectWS() {
    if (ws) return ws;   // 避免重复连接

    ws = new WebSocket("ws://localhost:7246/ws");

    ws.onopen = () => {
        console.log("WebSocket 已连接");
        ws.send("hello server");
    };

    // ws.onmessage = (msg) => {
    //     console.log("收到消息：", msg.data);
    // };

    ws.onmessage = (msg) => {
        console.log("收到消息：", msg.data);

        if (msg.data === "Update") {
            store.commit('SET_NEEDS_REFRESH', true);
            console.log("已通过 Vuex 提交刷新信号。");
        }
    };

    ws.onerror = (err) => {
        console.error("WebSocket错误", err);
    };

    ws.onclose = () => {
        console.log("WebSocket已关闭，3秒后尝试重连");
        ws = null;
        setTimeout(connectWS, 3000);
    };

    return ws;
}
