import styles from "./AudioRecorder.module.css";

const getMicrophoneInput = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    console.log("Microphone access granted");
    return stream;
  } catch (error) {
    const err = error as Error;
    console.error("Microphone access denied or error: " + err.message);
  }
};

let mediaRecorder: MediaRecorder;
let audioChunks: Blob[] = [];
let stream: MediaStream | undefined;
let audioContext: AudioContext;

const audioContainer = document.getElementById("audio-container");

type Visualizer = (
  analyser: AnalyserNode,
  dataArray: Uint8Array<ArrayBuffer>,
  bufferLength: number,
) => void;

const visualization: Visualizer = (analyser, dataArray, bufferLength) => {
  const canvas = document.getElementById("oscilloscope") as HTMLCanvasElement;
  if (!canvas) return;

  const canvasCtx = canvas.getContext("2d");
  if (!canvasCtx) return;

  const draw = () => {
    requestAnimationFrame(draw);

    analyser.getByteTimeDomainData(dataArray);

    canvasCtx.fillStyle = "rgb(200 200 200)";
    canvasCtx.fillRect(0, 0, canvas.width, canvas.height);

    canvasCtx.lineWidth = 2;
    canvasCtx.strokeStyle = "rgb(0 0 0)";

    canvasCtx.beginPath();

    const sliceWidth = (canvas.width * 1.0) / bufferLength;
    let x = 0;

    for (let i = 0; i < bufferLength; i++) {
      const v = dataArray[i] / 128.0;
      const y = (v * canvas.height) / 2;

      if (i === 0) {
        canvasCtx.moveTo(x, y);
      } else {
        canvasCtx.lineTo(x, y);
      }

      x += sliceWidth;
    }

    canvasCtx.lineTo(canvas.width, canvas.height / 2);
    canvasCtx.stroke();
  };

  draw();
};

const startRecording = async () => {
  stream = await getMicrophoneInput();
  if (!stream) return;

  audioContext = new AudioContext();

  const source = audioContext.createMediaStreamSource(stream);
  // TODO: Learn about FFT
  const analyser = audioContext.createAnalyser();
  analyser.fftSize = 2048;

  const bufferLength = analyser.frequencyBinCount;
  const dataArray = new Uint8Array(bufferLength);
  analyser.getByteTimeDomainData(dataArray);

  source.connect(analyser);

  visualization(analyser, dataArray, bufferLength);

  mediaRecorder = new MediaRecorder(stream);
  audioChunks = [];

  mediaRecorder.onstart = (_) => {
    if (audioContainer) audioContainer.innerHTML = "";

    console.log("Audio recording started");
  };
  mediaRecorder.start(5);

  mediaRecorder.ondataavailable = (e) => {
    audioChunks.push(e.data);
  };
};

const stopRecording = () => {
  mediaRecorder.onstop = (_) => {
    stream?.getTracks().forEach((track) => track.stop());
    audioContext.close();

    const audio = document.createElement("audio");

    if (audioContainer) audioContainer.append(audio);
    audio.controls = true;
    const blob = new Blob(audioChunks, { type: mediaRecorder.mimeType });
    const audioUrl = window.URL.createObjectURL(blob);
    audio.src = audioUrl;

    console.log("Audio recording stopped");
  };

  mediaRecorder.stop();
};

export default function AudioRecorder() {
  return (
    <div>
      <button className={`${styles.button}`} onClick={startRecording}>
        Start recording
      </button>
      <button className={`${styles.button}`} onClick={stopRecording}>
        Stop recording
      </button>
      <div id="audio-container" />
      <canvas id="oscilloscope" />
    </div>
  );
}
