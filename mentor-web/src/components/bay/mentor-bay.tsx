import { Suspense, useMemo } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import {
  ContactShadows,
  Environment,
  OrbitControls,
  PerspectiveCamera,
} from "@react-three/drei";
import * as THREE from "three";
import { XR, createXRStore } from "@react-three/xr";
import { SportTrac } from "./sport-trac";
import { BayLights, ShopBay } from "./shop";
import { jobById } from "@/lib/mentor/book";
import { useMentor } from "@/lib/mentor/store";

export const xrStore = createXRStore({ emulate: false, offerSession: false });

function CameraRig() {
  const jobId = useMentor((s) => s.jobId);
  const job = jobById(jobId);
  const { camera, controls } = useThree();
  const look = useMemo(
    () => new THREE.Vector3(...(job?.hotspot ?? [0, 0.72, 0.05])),
    [job],
  );
  const seat = useMemo(
    () =>
      job
        ? new THREE.Vector3(job.hotspot[0] + 2.35, job.hotspot[1] + 0.9, job.hotspot[2] + 1.55)
        : new THREE.Vector3(6.5, 1.22, 1.85),
    [job],
  );
  const settle = useMemo(() => ({ t: 1 }), [jobId]);

  useFrame((_, dt) => {
    if (settle.t <= 0) return;
    settle.t -= dt * 1.8;
    const c = controls as unknown as { target?: THREE.Vector3; update?: () => void } | null;
    if (!c?.target) return;
    c.target.lerp(look, 0.14);
    camera.position.lerp(seat, 0.1);
    c.update?.();
  });

  return null;
}

function Scene() {
  const paint = useMentor((s) => s.paint);
  const autoOrbit = useMentor((s) => s.autoOrbit);
  const stopOrbit = useMentor((s) => s.stopOrbit);

  return (
    <>
      <PerspectiveCamera makeDefault position={[6.5, 1.22, 1.85]} fov={30} />
      <BayLights />
      <Suspense fallback={null}>
        <Environment preset="warehouse" environmentIntensity={0.4} />
      </Suspense>
      <Suspense fallback={null}>
        <ShopBay />
      </Suspense>
      <SportTrac paintId={paint} />
      <CameraRig />
      <ContactShadows
        position={[0, 0.01, 0]}
        opacity={0.5}
        scale={10}
        blur={2.4}
        far={4}
      />
      <fog attach="fog" args={["#0c0d0b", 12, 28]} />
      <color attach="background" args={["#0c0d0b"]} />
      <OrbitControls
        makeDefault
        target={[0, 0.72, 0.05]}
        enableDamping
        dampingFactor={0.08}
        minDistance={2.4}
        maxDistance={12}
        minPolarAngle={0.25}
        maxPolarAngle={Math.PI / 2 - 0.05}
        autoRotate={autoOrbit}
        autoRotateSpeed={0.4}
        onStart={stopOrbit}
      />
    </>
  );
}

export function MentorBay() {
  return (
    <Canvas
      className="absolute inset-0 h-full w-full touch-none"
      shadows
      dpr={[1, 1.75]}
      gl={{ antialias: true, powerPreference: "high-performance" }}
      onPointerDown={() => useMentor.getState().stopOrbit()}
    >
      <XR store={xrStore}>
        <Scene />
      </XR>
    </Canvas>
  );
}
