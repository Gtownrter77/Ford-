import { useTexture } from "@react-three/drei";
import { RepeatWrapping, SRGBColorSpace } from "three";

export function ShopBay() {
  const floorMap = useTexture("/textures/floor.jpg");
  const wallMap = useTexture("/textures/wall.jpg");
  floorMap.wrapS = floorMap.wrapT = RepeatWrapping;
  floorMap.repeat.set(6, 6);
  floorMap.colorSpace = SRGBColorSpace;
  wallMap.wrapS = wallMap.wrapT = RepeatWrapping;
  wallMap.repeat.set(4, 2);
  wallMap.colorSpace = SRGBColorSpace;

  return (
    <group>
      <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, 0, 0]} receiveShadow>
        <planeGeometry args={[22, 22]} />
        <meshStandardMaterial map={floorMap} roughness={0.78} metalness={0.08} />
      </mesh>

      <mesh position={[0, 2.1, -7.2]} receiveShadow>
        <planeGeometry args={[22, 4.4]} />
        <meshStandardMaterial map={wallMap} roughness={0.9} metalness={0.02} />
      </mesh>
      <mesh position={[-7.4, 2.1, 0]} rotation={[0, Math.PI / 2, 0]} receiveShadow>
        <planeGeometry args={[14.4, 4.4]} />
        <meshStandardMaterial map={wallMap} roughness={0.9} metalness={0.02} />
      </mesh>
      <mesh position={[7.4, 2.1, 0]} rotation={[0, -Math.PI / 2, 0]} receiveShadow>
        <planeGeometry args={[14.4, 4.4]} />
        <meshStandardMaterial map={wallMap} roughness={0.9} metalness={0.02} />
      </mesh>

      <mesh position={[0, 4.25, -1]} rotation={[Math.PI / 2, 0, 0]}>
        <planeGeometry args={[22, 16]} />
        <meshStandardMaterial color="#161614" roughness={1} />
      </mesh>

      {[-3.2, 0, 3.2].map((x) => (
        <group key={x} position={[x, 4.05, -0.4]}>
          <mesh>
            <boxGeometry args={[1.8, 0.08, 0.55]} />
            <meshStandardMaterial color="#2a2a28" roughness={0.6} />
          </mesh>
          <mesh position={[0, -0.06, 0]}>
            <boxGeometry args={[1.6, 0.04, 0.4]} />
            <meshStandardMaterial
              color="#f2ead8"
              emissive="#f2ead8"
              emissiveIntensity={1.8}
            />
          </mesh>
        </group>
      ))}

      {/* CHARM plate on the back wall */}
      <mesh position={[0, 2.55, -7.18]} receiveShadow>
        <planeGeometry args={[2.4, 0.42]} />
        <meshStandardMaterial color="#1a1814" roughness={0.55} metalness={0.25} />
      </mesh>
      <mesh position={[0, 2.55, -7.16]}>
        <planeGeometry args={[2.28, 0.3]} />
        <meshStandardMaterial
          color="#c4b08a"
          emissive="#c4b08a"
          emissiveIntensity={0.18}
          roughness={0.4}
          metalness={0.45}
        />
      </mesh>
      {[-1, 1].map((s) => (
        <mesh key={s} position={[5.8 * s, 1.1, -6.4]} castShadow receiveShadow>
          <boxGeometry args={[1.4, 2.2, 0.55]} />
          <meshStandardMaterial color="#2c3034" roughness={0.55} metalness={0.3} />
        </mesh>
      ))}
    </group>
  );
}

export function BayLights() {
  return (
    <>
      <hemisphereLight args={["#d4dce0", "#1a1612", 0.55]} />
      <ambientLight intensity={0.32} />
      <directionalLight
        position={[4.5, 7, 3.5]}
        intensity={1.35}
        castShadow
        shadow-mapSize={[2048, 2048]}
        shadow-camera-near={1}
        shadow-camera-far={24}
        shadow-camera-left={-8}
        shadow-camera-right={8}
        shadow-camera-top={8}
        shadow-camera-bottom={-8}
      />
      <spotLight
        position={[-2.8, 4.1, 3.2]}
        angle={0.55}
        penumbra={0.6}
        intensity={40}
        distance={14}
        color="#f0e6d4"
        castShadow
      />
      <spotLight
        position={[3.4, 4.1, -1.4]}
        angle={0.5}
        penumbra={0.7}
        intensity={28}
        distance={12}
        color="#dce4ea"
      />
      <pointLight position={[0, 1.2, 3.2]} intensity={8} distance={6} color="#d8dde2" />
    </>
  );
}
