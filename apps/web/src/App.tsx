import AudioRecorder from "./AudioRecorder/AudioRecorder";

function App() {
  const sendPing = (): void => {
    const socket = new WebSocket("/ws");

    socket.onopen = (_) => {
      console.log("WebSocket connection established");
      sendMessage("ping");
    };

    socket.onmessage = (event: MessageEvent) => {
      const messageData = JSON.parse(event.data);
      console.log(messageData);
    };

    socket.onerror = (error: Event) => {
      console.error("Error received: ", error);
    };

    const sendMessage = (message: string) => {
      if (socket.readyState == socket.OPEN) {
        socket.send(JSON.stringify({ message: message }));
      } else {
        console.error("WebSocket connection is not ready");
      }
    };

    window.addEventListener("beforeunload", () => {
      socket.close();
    });
  };

  return (
    <>
      <span>Click here to test WS: </span>
      <button onClick={sendPing}>Click me!</button>

      <AudioRecorder />
    </>
  );
}

export default App;
