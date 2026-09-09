import { useEffect, useMemo, useRef } from "react";
import { useFrame } from "@react-three/fiber";
import { RoundedBox } from "@react-three/drei";
import * as THREE from "three";
import { jobById } from "@/lib/mentor/book";
import { useMentor } from "@/lib/mentor/store";
import {
  BED_IN_H,
  BED_IN_W,
  BED_LEN,
  BODY_W,
  CAB_REAR_Z,
  COWL_Z,
  FRONT_AXLE_Z,
  HEIGHT,
  LOAD_H,
  NOSE_Z,
  PAINTS,
  REAR_AXLE_Z,
  TAIL_Z,
  TIRE_R,
  TIRE_W,
  TRACK_F,
  TRACK_R,
  WHEEL_R,
  type PaintId,
} from "@/lib/mentor/scale";

type Mats = {
  paint: THREE.MeshPhysicalMaterial;
  cladding: THREE.MeshStandardMaterial;
  chrome: THREE.MeshStandardMaterial;
  dark: THREE.MeshStandardMaterial;
  glass: THREE.MeshPhysicalMaterial;
  rubber: THREE.MeshStandardMaterial;
  lens: THREE.MeshPhysicalMaterial;
  amber: THREE.MeshStandardMaterial;
  red: THREE.MeshStandardMaterial;
  interior: THREE.MeshStandardMaterial;
  plate: THREE.MeshStandardMaterial;
  silver: THREE.MeshStandardMaterial;
  oval: THREE.MeshStandardMaterial;
  badge: THREE.MeshStandardMaterial;
  leather: THREE.MeshStandardMaterial;
};

const SILL = 0.38;
const BELT = 1.02;
const ROOF = HEIGHT - 0.08;
const BED_RAIL = 1.1;
const CAB_Z = (COWL_Z + CAB_REAR_Z) / 2;
const CAB_LEN = COWL_Z - CAB_REAR_Z;
const BED_Z = TAIL_Z + BED_LEN / 2;
const HOOD_LEN = NOSE_Z - COWL_Z - 0.12;
const GLASS_H = ROOF - 0.08 - BELT;

function canvasTex(
  w: number,
  h: number,
  draw: (ctx: CanvasRenderingContext2D) => void,
) {
  const c = document.createElement("canvas");
  c.width = w;
  c.height = h;
  const ctx = c.getContext("2d");
  if (ctx) draw(ctx);
  const t = new THREE.CanvasTexture(c);
  t.colorSpace = THREE.SRGBColorSpace;
  t.anisotropy = 8;
  return t;
}

function useMats(paintId: PaintId): Mats {
  const color = PAINTS[paintId].color;
  const ovalMap = useMemo(
    () =>
      canvasTex(256, 128, (ctx) => {
        ctx.clearRect(0, 0, 256, 128);
        ctx.fillStyle = "#163e82";
        ctx.beginPath();
        ctx.ellipse(128, 64, 118, 56, 0, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = "#d7dde4";
        ctx.lineWidth = 10;
        ctx.stroke();
        ctx.fillStyle = "#f4f6f8";
        ctx.font = "700 52px 'IBM Plex Sans Condensed', Arial, sans-serif";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText("FORD", 128, 66);
      }),
    [],
  );
  const badgeMap = useMemo(
    () =>
      canvasTex(512, 128, (ctx) => {
        ctx.fillStyle = "#8b1a1d";
        ctx.beginPath();
        ctx.roundRect(8, 18, 496, 92, 14);
        ctx.fill();
        ctx.strokeStyle = "#e8e4d8";
        ctx.lineWidth = 6;
        ctx.stroke();
        ctx.fillStyle = "#f4f0e6";
        ctx.font = "700 48px 'IBM Plex Sans Condensed', Arial, sans-serif";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText("SPORT TRAC", 256, 66);
      }),
    [],
  );

  const mats = useMemo(() => {
    return {
      paint: new THREE.MeshPhysicalMaterial({
        color,
        metalness: 0.36,
        roughness: 0.3,
        clearcoat: 1,
        clearcoatRoughness: 0.12,
      }),
      cladding: new THREE.MeshStandardMaterial({
        color: "#1c1e20",
        metalness: 0.1,
        roughness: 0.8,
      }),
      chrome: new THREE.MeshStandardMaterial({
        color: "#d4dae0",
        metalness: 1,
        roughness: 0.14,
      }),
      dark: new THREE.MeshStandardMaterial({
        color: "#121314",
        metalness: 0.32,
        roughness: 0.55,
      }),
      glass: new THREE.MeshPhysicalMaterial({
        color: "#24343c",
        metalness: 0.08,
        roughness: 0.06,
        transparent: true,
        opacity: 0.55,
        envMapIntensity: 1.8,
      }),
      rubber: new THREE.MeshStandardMaterial({
        color: "#141414",
        roughness: 0.94,
        metalness: 0.04,
      }),
      lens: new THREE.MeshPhysicalMaterial({
        color: "#eef3f7",
        roughness: 0.06,
        metalness: 0.12,
        transparent: true,
        opacity: 0.9,
      }),
      amber: new THREE.MeshStandardMaterial({
        color: "#c47a22",
        emissive: "#c47a22",
        emissiveIntensity: 0.32,
        roughness: 0.32,
      }),
      red: new THREE.MeshStandardMaterial({
        color: "#8a1518",
        emissive: "#4a080a",
        emissiveIntensity: 0.38,
        roughness: 0.3,
      }),
      interior: new THREE.MeshStandardMaterial({
        color: "#1c1a18",
        roughness: 0.9,
      }),
      plate: new THREE.MeshStandardMaterial({
        color: "#d8d2c4",
        roughness: 0.55,
        metalness: 0.08,
      }),
      silver: new THREE.MeshStandardMaterial({
        color: "#b8bdc4",
        metalness: 0.88,
        roughness: 0.28,
      }),
      oval: new THREE.MeshStandardMaterial({
        map: ovalMap,
        roughness: 0.32,
        metalness: 0.22,
        transparent: true,
      }),
      badge: new THREE.MeshStandardMaterial({
        map: badgeMap,
        roughness: 0.4,
        metalness: 0.15,
      }),
      leather: new THREE.MeshStandardMaterial({
        color: "#2a2622",
        roughness: 0.72,
      }),
    };
  }, [color, ovalMap, badgeMap]);

  useEffect(() => {
    return () => {
      ovalMap.dispose();
      badgeMap.dispose();
      for (const m of Object.values(mats)) m.dispose();
    };
  }, [mats, ovalMap, badgeMap]);

  return mats;
}

function Wheel({ x, z, mats }: { x: number; z: number; mats: Mats }) {
  const side = x > 0 ? 1 : -1;
  return (
    <group position={[x, TIRE_R, z]}>
      <mesh rotation={[0, 0, Math.PI / 2]} castShadow material={mats.rubber}>
        <cylinderGeometry args={[TIRE_R, TIRE_R, TIRE_W, 36]} />
      </mesh>
      <mesh rotation={[0, 0, Math.PI / 2]} material={mats.dark}>
        <cylinderGeometry args={[TIRE_R * 0.72, TIRE_R * 0.72, TIRE_W * 0.58, 24]} />
      </mesh>
      <mesh rotation={[0, 0, Math.PI / 2]} material={mats.silver}>
        <cylinderGeometry args={[WHEEL_R * 0.94, WHEEL_R * 0.94, TIRE_W * 0.34, 28]} />
      </mesh>
      {[0, 1, 2, 3, 4].map((i) => (
        <mesh
          key={i}
          rotation={[0, (i * Math.PI * 2) / 5, 0]}
          position={[side * TIRE_W * 0.08, 0, 0]}
          castShadow
          material={mats.silver}
        >
          <boxGeometry args={[0.055, 0.05, WHEEL_R * 1.22]} />
        </mesh>
      ))}
      <mesh
        position={[side * TIRE_W * 0.16, 0, 0]}
        rotation={[0, Math.PI / 2, 0]}
        material={mats.dark}
      >
        <circleGeometry args={[WHEEL_R * 0.28, 20]} />
      </mesh>
      <mesh
        position={[side * TIRE_W * 0.17, 0, 0]}
        rotation={[0, Math.PI / 2, 0]}
        material={mats.oval}
      >
        <planeGeometry args={[0.09, 0.04]} />
      </mesh>
    </group>
  );
}

function Hood({ mats, open }: { mats: Mats; open: boolean }) {
  const ref = useRef<THREE.Group>(null);
  useFrame((_, dt) => {
    if (!ref.current) return;
    const cap = Math.min(dt, 0.05);
    ref.current.rotation.x = THREE.MathUtils.damp(
      ref.current.rotation.x,
      open ? -0.7 : -0.1,
      5,
      cap,
    );
  });
  const len = HOOD_LEN;
  return (
    <group ref={ref} position={[0, 1.16, COWL_Z - 0.04]}>
      <RoundedBox
        args={[BODY_W - 0.28, 0.045, len]}
        radius={0.02}
        smoothness={2}
        position={[0, 0, -len / 2]}
        castShadow
        receiveShadow
        material={mats.paint}
      />
      <mesh position={[0, -0.01, -len * 0.35]} material={mats.dark}>
        <boxGeometry args={[0.55, 0.02, len * 0.45]} />
      </mesh>
    </group>
  );
}

function EngineBay({ mats }: { mats: Mats }) {
  return (
    <group position={[0, 0.72, (COWL_Z + NOSE_Z) / 2 - 0.05]}>
      <mesh position={[0, 0.08, 0.22]} material={mats.dark} castShadow>
        <boxGeometry args={[0.62, 0.28, 0.08]} />
      </mesh>
      <mesh position={[0, 0.08, 0.3]} material={mats.silver}>
        <boxGeometry args={[0.58, 0.24, 0.03]} />
      </mesh>
      <mesh position={[-0.22, 0.12, -0.05]} material={mats.dark} castShadow>
        <boxGeometry args={[0.32, 0.22, 0.42]} />
      </mesh>
      <mesh position={[0.28, 0.02, -0.08]} material={mats.cladding} castShadow>
        <boxGeometry args={[0.2, 0.16, 0.22]} />
      </mesh>
      <mesh position={[0.28, 0.02, 0.06]} rotation={[Math.PI / 2, 0, 0]} material={mats.dark}>
        <cylinderGeometry args={[0.085, 0.085, 0.05, 20]} />
      </mesh>
      <mesh position={[0.28, 0.18, -0.08]} rotation={[0, 0, Math.PI / 2]} material={mats.rubber}>
        <torusGeometry args={[0.1, 0.012, 8, 18]} />
      </mesh>
    </group>
  );
}

export function SportTrac({ paintId }: { paintId: PaintId }) {
  const mats = useMats(paintId);
  const jobId = useMentor((s) => s.jobId);
  const hvacOpen =
    !!jobId &&
    (jobId.startsWith("ac") ||
      jobId.startsWith("hvac") ||
      jobId === "heater-core");

  return (
    <group name="sport-trac-2004">
      <Wheel x={TRACK_F / 2} z={FRONT_AXLE_Z} mats={mats} />
      <Wheel x={-TRACK_F / 2} z={FRONT_AXLE_Z} mats={mats} />
      <Wheel x={TRACK_R / 2} z={REAR_AXLE_Z} mats={mats} />
      <Wheel x={-TRACK_R / 2} z={REAR_AXLE_Z} mats={mats} />

      {/* rocker / lower body between axles */}
      <RoundedBox
        args={[BODY_W - 0.08, 0.42, CAB_LEN + 0.15]}
        radius={0.03}
        smoothness={2}
        position={[0, SILL + 0.22, CAB_Z + 0.04]}
        castShadow
        receiveShadow
        material={mats.paint}
      />
      <mesh position={[0, 0.52, CAB_Z + 0.04]} receiveShadow material={mats.cladding}>
        <boxGeometry args={[BODY_W + 0.04, 0.2, CAB_LEN * 0.82]} />
      </mesh>

      {/* front clip under hood */}
      <RoundedBox
        args={[BODY_W - 0.22, 0.38, HOOD_LEN * 0.55]}
        radius={0.03}
        smoothness={2}
        position={[0, 0.72, NOSE_Z - HOOD_LEN * 0.42]}
        castShadow
        material={mats.paint}
      />

      <EngineBay mats={mats} />
      <Hood mats={mats} open={hvacOpen} />

      {/* Explorer face — 3-bar grille, 02–05 */}
      <RoundedBox
        args={[BODY_W - 0.2, 0.38, 0.16]}
        radius={0.04}
        smoothness={3}
        position={[0, 0.48, NOSE_Z + 0.02]}
        castShadow
        material={mats.paint}
      />
      <RoundedBox
        args={[0.82, 0.32, 0.08]}
        radius={0.03}
        smoothness={3}
        position={[0, 0.84, NOSE_Z + 0.01]}
        material={mats.dark}
      />
      {[0.7, 0.82, 0.94].map((y) => (
        <mesh key={y} position={[0, y, NOSE_Z + 0.055]} material={mats.chrome}>
          <boxGeometry args={[0.78, 0.018, 0.03]} />
        </mesh>
      ))}
      {[-0.2, 0.2].map((x) => (
        <mesh key={x} position={[x, 0.82, NOSE_Z + 0.05]} material={mats.chrome}>
          <boxGeometry args={[0.012, 0.26, 0.02]} />
        </mesh>
      ))}
      <mesh position={[0, 0.82, NOSE_Z + 0.07]} material={mats.oval}>
        <planeGeometry args={[0.3, 0.13]} />
      </mesh>

      {[-1, 1].map((s) => (
        <group key={`hl${s}`}>
          <RoundedBox
            args={[0.4, 0.2, 0.12]}
            radius={0.035}
            smoothness={4}
            position={[(BODY_W / 2 - 0.34) * s, 0.84, NOSE_Z - 0.02]}
            material={mats.lens}
          />
          <mesh
            position={[(BODY_W / 2 - 0.18) * s, 0.84, NOSE_Z + 0.04]}
            material={mats.amber}
          >
            <boxGeometry args={[0.09, 0.16, 0.05]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 - 0.02) * s, 0.84, NOSE_Z - 0.08]}
            material={mats.amber}
          >
            <boxGeometry args={[0.04, 0.14, 0.1]} />
          </mesh>
        </group>
      ))}

      {[-1, 1].map((s) => (
        <mesh
          key={`fog${s}`}
          position={[0.44 * s, 0.44, NOSE_Z + 0.1]}
          rotation={[Math.PI / 2, 0, 0]}
          material={mats.lens}
        >
          <cylinderGeometry args={[0.055, 0.055, 0.04, 16]} />
        </mesh>
      ))}

      {/* front fenders */}
      {[-1, 1].map((s) => (
        <group key={`ff${s}`}>
          <RoundedBox
            args={[0.22, 0.55, 0.95]}
            radius={0.04}
            smoothness={3}
            position={[(BODY_W / 2 - 0.08) * s, 0.78, FRONT_AXLE_Z + 0.06]}
            castShadow
            material={mats.paint}
          />
        </group>
      ))}

      {/* cabin interior */}
      <mesh position={[0, 0.88, CAB_Z]} material={mats.interior}>
        <boxGeometry args={[BODY_W - 0.3, 0.62, CAB_LEN - 0.5]} />
      </mesh>
      {[-0.22, 0.22].map((x) =>
        [CAB_Z + 0.38, CAB_Z - 0.42].map((z) => (
          <RoundedBox
            key={`${x}${z}`}
            args={[0.38, 0.42, 0.42]}
            radius={0.04}
            smoothness={2}
            position={[x, 0.78, z]}
            material={mats.leather}
          />
        )),
      )}
      <mesh position={[0, 1.12, COWL_Z - 0.18]} rotation={[-0.4, 0, 0]} material={mats.dark}>
        <boxGeometry args={[0.95, 0.08, 0.38]} />
      </mesh>
      <mesh position={[0.18, 1.05, COWL_Z - 0.08]} rotation={[1.15, 0, 0]} material={mats.dark}>
        <torusGeometry args={[0.14, 0.016, 8, 20]} />
      </mesh>

      {/* windshield */}
      <mesh
        position={[0, BELT + GLASS_H * 0.46, COWL_Z - 0.16]}
        rotation={[-0.52, 0, 0]}
        castShadow
        material={mats.glass}
      >
        <boxGeometry args={[BODY_W - 0.36, GLASS_H * 0.98, 0.032]} />
      </mesh>
      {[-1, 1].map((s) => (
        <mesh
          key={`ap${s}`}
          position={[(BODY_W / 2 - 0.13) * s, BELT + GLASS_H * 0.5, COWL_Z - 0.14]}
          rotation={[-0.5, 0, 0.08 * s]}
          castShadow
          material={mats.paint}
        >
          <boxGeometry args={[0.07, GLASS_H + 0.1, 0.1]} />
        </mesh>
      ))}

      {/* side glass + B-pillar — crew cab */}
      {[-1, 1].map((s) => (
        <group key={`sg${s}`}>
          <mesh
            position={[(BODY_W / 2 - 0.05) * s, BELT + GLASS_H * 0.48, COWL_Z - 0.78]}
            material={mats.glass}
          >
            <boxGeometry args={[0.032, GLASS_H * 0.9, 0.95]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 - 0.05) * s, BELT + GLASS_H * 0.48, CAB_REAR_Z + 0.58]}
            material={mats.glass}
          >
            <boxGeometry args={[0.032, GLASS_H * 0.9, 0.72]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 - 0.04) * s, BELT + GLASS_H * 0.5, CAB_Z - 0.04]}
            material={mats.dark}
          >
            <boxGeometry args={[0.055, GLASS_H + 0.04, 0.09]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.02) * s, BELT + 0.01, COWL_Z - 0.72]}
            material={mats.chrome}
          >
            <boxGeometry args={[0.028, 0.032, 0.13]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.02) * s, BELT + 0.01, CAB_REAR_Z + 0.52]}
            material={mats.chrome}
          >
            <boxGeometry args={[0.028, 0.032, 0.13]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.01) * s, 0.92, COWL_Z - 0.55]}
            material={mats.paint}
          >
            <boxGeometry args={[0.04, 0.07, 0.12]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.01) * s, 0.92, CAB_REAR_Z + 0.72]}
            material={mats.paint}
          >
            <boxGeometry args={[0.04, 0.07, 0.12]} />
          </mesh>
        </group>
      ))}

      {/* cab rear glass — power drop window */}
      <mesh position={[0, BELT + GLASS_H * 0.44, CAB_REAR_Z + 0.05]} material={mats.glass}>
        <boxGeometry args={[BODY_W - 0.38, GLASS_H * 0.78, 0.03]} />
      </mesh>
      <mesh position={[0, BELT + 0.04, CAB_REAR_Z + 0.05]} material={mats.dark}>
        <boxGeometry args={[BODY_W - 0.4, 0.04, 0.04]} />
      </mesh>

      <RoundedBox
        args={[BODY_W - 0.24, 0.055, CAB_LEN - 0.62]}
        radius={0.02}
        smoothness={3}
        position={[0, ROOF, CAB_Z - 0.08]}
        castShadow
        material={mats.paint}
      />
      {[-1, 1].map((s) => (
        <mesh
          key={`dp${s}`}
          position={[(BODY_W / 2 - 0.1) * s, BELT + GLASS_H * 0.5, CAB_REAR_Z + 0.12]}
          castShadow
          material={mats.paint}
        >
          <boxGeometry args={[0.1, GLASS_H + 0.12, 0.18]} />
        </mesh>
      ))}
      <mesh position={[0, ROOF - 0.05, CAB_REAR_Z - 0.01]} material={mats.red}>
        <boxGeometry args={[0.52, 0.032, 0.04]} />
      </mesh>

      {/* short composite bed */}
      <mesh position={[0, LOAD_H, BED_Z]} receiveShadow castShadow material={mats.cladding}>
        <boxGeometry args={[BED_IN_W + 0.04, 0.03, BED_LEN - 0.06]} />
      </mesh>
      {[-1, 1].map((s) => (
        <group key={`bed${s}`}>
          <RoundedBox
            args={[0.09, BED_IN_H, BED_LEN - 0.04]}
            radius={0.02}
            smoothness={2}
            position={[(BED_IN_W / 2 + 0.03) * s, LOAD_H + BED_IN_H / 2, BED_Z]}
            castShadow
            material={mats.paint}
          />
          <mesh
            position={[(BED_IN_W / 2 + 0.03) * s, BED_RAIL + 0.02, BED_Z]}
            material={mats.cladding}
          >
            <boxGeometry args={[0.11, 0.045, BED_LEN]} />
          </mesh>
          <RoundedBox
            args={[0.2, 0.5, 0.7]}
            radius={0.04}
            smoothness={2}
            position={[(BODY_W / 2 - 0.08) * s, 0.78, REAR_AXLE_Z]}
            castShadow
            material={mats.paint}
          />
        </group>
      ))}
      <RoundedBox
        args={[BED_IN_W + 0.1, BED_IN_H - 0.04, 0.07]}
        radius={0.02}
        smoothness={2}
        position={[0, LOAD_H + BED_IN_H / 2, TAIL_Z + 0.04]}
        castShadow
        material={mats.paint}
      />
      <mesh position={[0, BED_RAIL + 0.02, TAIL_Z + 0.04]} material={mats.cladding}>
        <boxGeometry args={[BED_IN_W + 0.12, 0.045, 0.1]} />
      </mesh>
      <mesh position={[0, LOAD_H + 0.18, TAIL_Z + 0.09]} material={mats.chrome}>
        <boxGeometry args={[0.28, 0.035, 0.03]} />
      </mesh>
      <mesh position={[0, 0.78, TAIL_Z + 0.085]} material={mats.badge}>
        <planeGeometry args={[0.42, 0.1]} />
      </mesh>
      <mesh position={[0, 0.68, TAIL_Z + 0.09]} material={mats.oval}>
        <planeGeometry args={[0.2, 0.09]} />
      </mesh>

      {[-1, 1].map((s) => (
        <group key={`tl${s}`}>
          <RoundedBox
            args={[0.08, 0.38, 0.16]}
            radius={0.02}
            smoothness={2}
            position={[(BED_IN_W / 2 + 0.01) * s, LOAD_H + 0.32, TAIL_Z + 0.07]}
            material={mats.red}
          />
          <mesh
            position={[(BED_IN_W / 2 + 0.05) * s, LOAD_H + 0.32, TAIL_Z + 0.16]}
            material={mats.red}
          >
            <boxGeometry args={[0.04, 0.32, 0.1]} />
          </mesh>
        </group>
      ))}

      <RoundedBox
        args={[BODY_W - 0.16, 0.18, 0.2]}
        radius={0.04}
        smoothness={3}
        position={[0, 0.44, TAIL_Z - 0.05]}
        castShadow
        material={mats.paint}
      />
      <mesh position={[0, 0.38, TAIL_Z - 0.14]} material={mats.dark}>
        <boxGeometry args={[0.28, 0.07, 0.14]} />
      </mesh>
      <mesh position={[0, 0.54, TAIL_Z - 0.16]} material={mats.plate}>
        <boxGeometry args={[0.32, 0.16, 0.02]} />
      </mesh>
      <mesh position={[-0.22, 0.32, TAIL_Z - 0.08]} rotation={[0, 0, Math.PI / 2]} material={mats.dark}>
        <cylinderGeometry args={[0.028, 0.032, 0.08, 12]} />
      </mesh>

      {[-1, 1].map((s) => (
        <group key={`rr${s}`}>
          <mesh
            position={[(BODY_W / 2 - 0.3) * s, ROOF + 0.045, CAB_Z + 0.02]}
            castShadow
            material={mats.dark}
          >
            <boxGeometry args={[0.03, 0.022, CAB_LEN - 0.72]} />
          </mesh>
          {[0.52, -0.52].map((z) => (
            <mesh
              key={z}
              position={[(BODY_W / 2 - 0.3) * s, ROOF + 0.018, CAB_Z + z]}
              material={mats.dark}
            >
              <boxGeometry args={[0.042, 0.05, 0.05]} />
            </mesh>
          ))}
        </group>
      ))}

      {[-1, 1].map((s) => (
        <group key={`m${s}`} position={[(BODY_W / 2) * s, 1.14, COWL_Z - 0.12]}>
          <mesh position={[0.11 * s, 0, 0]} material={mats.paint} castShadow>
            <boxGeometry args={[0.2, 0.13, 0.22]} />
          </mesh>
          <mesh position={[0.14 * s, 0, 0.02]} material={mats.chrome}>
            <boxGeometry args={[0.02, 0.1, 0.16]} />
          </mesh>
        </group>
      ))}

      {[-1, 1].map((s) => (
        <mesh
          key={`rb${s}`}
          position={[(BODY_W / 2 + 0.05) * s, 0.33, CAB_Z + 0.06]}
          castShadow
          material={mats.dark}
        >
          <boxGeometry args={[0.13, 0.032, CAB_LEN * 0.7]} />
        </mesh>
      ))}

      <mesh position={[0.18, 1.13, COWL_Z + 0.02]} rotation={[0, 0, -0.2]} material={mats.dark}>
        <boxGeometry args={[0.62, 0.012, 0.016]} />
      </mesh>
      <mesh position={[-0.16, 1.12, COWL_Z]} rotation={[0, 0, 0.24]} material={mats.dark}>
        <boxGeometry args={[0.52, 0.012, 0.016]} />
      </mesh>

      <mesh position={[BODY_W / 2 - 0.12, 1.38, COWL_Z - 0.22]} material={mats.dark}>
        <cylinderGeometry args={[0.006, 0.004, 0.58, 8]} />
      </mesh>

      <mesh
        position={[-BODY_W / 2 + 0.04, 0.88, CAB_REAR_Z + 0.35]}
        material={mats.paint}
      >
        <boxGeometry args={[0.02, 0.16, 0.22]} />
      </mesh>

      <CharmHotspots />
    </group>
  );
}

function CharmHotspots() {
  const jobId = useMentor((s) => s.jobId);
  const setJob = useMentor((s) => s.setJob);
  const job = jobById(jobId);
  const mark = job?.hotspot ?? [0.42, 0.58, 1.52];
  const lit = !!jobId && (jobId.startsWith("ac") || jobId.startsWith("hvac") || jobId === "heater-core");

  return (
    <group>
      <mesh
        position={[0.28, 0.74, (COWL_Z + NOSE_Z) / 2]}
        castShadow
        onClick={(e) => {
          e.stopPropagation();
          setJob("ac-compressor");
        }}
      >
        <boxGeometry args={[0.2, 0.16, 0.22]} />
        <meshStandardMaterial
          color="#2a3034"
          metalness={0.55}
          roughness={0.4}
          emissive={lit ? "#9aa3a7" : "#000000"}
          emissiveIntensity={lit ? 0.4 : 0}
        />
      </mesh>
      {job ? (
        <mesh position={mark}>
          <sphereGeometry args={[0.032, 16, 16]} />
          <meshStandardMaterial color="#e8e4d8" emissive="#e8e4d8" emissiveIntensity={0.85} />
        </mesh>
      ) : null}
    </group>
  );
}
