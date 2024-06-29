const connect = () => {
  const socket = new WebSocket("ws://localhost:5001");

  socket.onopen = (event) =>
    console.log("Connected to WebSocket server");

  socket.onmessage = (event) => {
    console.log("Message from server:", event.data);

    if (event.data === "reload") {
      window.location.reload();
    }
  };

  socket.onclose = () => {
    console.log("Disconnected from WebSocket server");

    setTimeout(connect, 1000);
  };
};

connect();
