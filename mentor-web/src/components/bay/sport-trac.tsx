import { useEffect, useMemo } from "react";
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
};

const SILL = 0.38;
const BELT = 1.05;
const ROOF = HEIGHT - 0.09;
const BED_RAIL = 1.11;
const ARCH = TIRE_R + 0.1;
const CAB_Z = (COWL_Z + CAB_REAR_Z) / 2;
const CAB_LEN = COWL_Z - CAB_REAR_Z;
const BED_Z = TAIL_Z + BED_LEN / 2;

function tracHull(): THREE.Shape {
  const s = new THREE.Shape();
  s.moveTo(TAIL_Z - 0.05, 0.35);
  s.lineTo(TAIL_Z - 0.11, 0.46);
  s.lineTo(TAIL_Z - 0.08, 0.54);
  s.lineTo(TAIL_Z + 0.03, 0.56);
  s.lineTo(TAIL_Z + 0.03, BED_RAIL);
  s.lineTo(CAB_REAR_Z + 0.08, BED_RAIL);
  s.lineTo(CAB_REAR_Z + 0.14, BELT);
  s.lineTo(COWL_Z + 0.04, BELT);
  s.lineTo(COWL_Z + 0.12, 1.13);
  s.lineTo(NOSE_Z - 0.28, 0.98);
  s.lineTo(NOSE_Z - 0.06, 0.9);
  s.lineTo(NOSE_Z + 0.01, 0.68);
  s.lineTo(NOSE_Z + 0.1, 0.5);
  s.lineTo(NOSE_Z + 0.12, 0.34);
  s.lineTo(FRONT_AXLE_Z + ARCH, SILL);
  s.absarc(FRONT_AXLE_Z, SILL, ARCH, 0, Math.PI, true);
  s.lineTo(REAR_AXLE_Z + ARCH, SILL);
  s.absarc(REAR_AXLE_Z, SILL, ARCH, 0, Math.PI, true);
  s.lineTo(TAIL_Z - 0.05, 0.35);
  s.closePath();
  return s;
}

function useMats(paintId: PaintId): Mats {
  const color = PAINTS[paintId].color;
  const ovalMap = useMemo(() => {
    const c = document.createElement("canvas");
    c.width = 256;
    c.height = 128;
    const ctx = c.getContext("2d");
    if (ctx) {
      ctx.clearRect(0, 0, 256, 128);
      ctx.fillStyle = "#163e82";
      ctx.beginPath();
      ctx.ellipse(128, 64, 120, 58, 0, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = "#d7dde4";
      ctx.lineWidth = 8;
      ctx.stroke();
      ctx.fillStyle = "#f4f6f8";
      ctx.font = "700 54px 'IBM Plex Sans Condensed', Arial, sans-serif";
      ctx.textAlign = "center";
      ctx.textBaseline = "middle";
      ctx.fillText("FORD", 128, 66);
    }
    const t = new THREE.CanvasTexture(c);
    t.colorSpace = THREE.SRGBColorSpace;
    t.anisotropy = 8;
    return t;
  }, []);

  const mats = useMemo(() => {
    return {
      paint: new THREE.MeshPhysicalMaterial({
        color,
        metalness: 0.38,
        roughness: 0.32,
        clearcoat: 1,
        clearcoatRoughness: 0.14,
      }),
      cladding: new THREE.MeshStandardMaterial({
        color: "#1a1c1e",
        metalness: 0.12,
        roughness: 0.78,
      }),
      chrome: new THREE.MeshStandardMaterial({
        color: "#cfd6dd",
        metalness: 1,
        roughness: 0.16,
      }),
      dark: new THREE.MeshStandardMaterial({
        color: "#121314",
        metalness: 0.35,
        roughness: 0.55,
      }),
      glass: new THREE.MeshPhysicalMaterial({
        color: "#2a3a44",
        metalness: 0.1,
        roughness: 0.08,
        transparent: true,
        opacity: 0.62,
        envMapIntensity: 1.6,
      }),
      rubber: new THREE.MeshStandardMaterial({
        color: "#161616",
        roughness: 0.94,
        metalness: 0.04,
      }),
      lens: new THREE.MeshPhysicalMaterial({
        color: "#e8eef3",
        roughness: 0.08,
        metalness: 0.15,
        transparent: true,
        opacity: 0.88,
        transmission: 0.15,
      }),
      amber: new THREE.MeshStandardMaterial({
        color: "#c47a22",
        emissive: "#c47a22",
        emissiveIntensity: 0.28,
        roughness: 0.35,
      }),
      red: new THREE.MeshStandardMaterial({
        color: "#8a1518",
        emissive: "#4a080a",
        emissiveIntensity: 0.35,
        roughness: 0.32,
      }),
      interior: new THREE.MeshStandardMaterial({
        color: "#1a1816",
        roughness: 0.88,
      }),
      plate: new THREE.MeshStandardMaterial({
        color: "#d8d2c4",
        roughness: 0.55,
        metalness: 0.08,
      }),
      silver: new THREE.MeshStandardMaterial({
        color: "#b8bdc4",
        metalness: 0.85,
        roughness: 0.32,
      }),
      oval: new THREE.MeshStandardMaterial({
        map: ovalMap,
        roughness: 0.35,
        metalness: 0.2,
      }),
    };
  }, [color, ovalMap]);

  useEffect(() => {
    return () => {
      ovalMap.dispose();
      for (const m of Object.values(mats)) m.dispose();
    };
  }, [mats, ovalMap]);

  return mats;
}

function Wheel({ x, z, mats }: { x: number; z: number; mats: Mats }) {
  const side = x > 0 ? 1 : -1;
  return (
    <group position={[x, TIRE_R, z]}>
      <mesh rotation={[0, 0, Math.PI / 2]} castShadow material={mats.rubber}>
        <cylinderGeometry args={[TIRE_R, TIRE_R, TIRE_W, 40]} />
      </mesh>
      <mesh rotation={[0, 0, Math.PI / 2]} material={mats.dark}>
        <cylinderGeometry args={[TIRE_R * 0.7, TIRE_R * 0.7, TIRE_W * 0.62, 28]} />
      </mesh>
      <mesh rotation={[0, 0, Math.PI / 2]} material={mats.silver}>
        <cylinderGeometry args={[WHEEL_R * 0.92, WHEEL_R * 0.92, TIRE_W * 0.42, 28]} />
      </mesh>
      <mesh rotation={[0, 0, Math.PI / 2]} material={mats.dark}>
        <cylinderGeometry args={[WHEEL_R * 0.28, WHEEL_R * 0.28, TIRE_W * 0.5, 16]} />
      </mesh>
      {[0, 1, 2, 3, 4].map((i) => (
        <mesh
          key={i}
          rotation={[0, (i * Math.PI * 2) / 5, 0]}
          position={[side * TIRE_W * 0.06, 0, 0]}
          castShadow
          material={mats.silver}
        >
          <boxGeometry args={[0.07, 0.045, WHEEL_R * 1.15]} />
        </mesh>
      ))}
    </group>
  );
}

function Hull({ mats }: { mats: Mats }) {
  const paint = useMemo(() => {
    const shape = tracHull();
    const depth = BODY_W - 0.14;
    const paintGeo = new THREE.ExtrudeGeometry(shape, {
      depth,
      bevelEnabled: true,
      bevelThickness: 0.03,
      bevelSize: 0.024,
      bevelSegments: 2,
      curveSegments: 12,
    });
    paintGeo.rotateY(-Math.PI / 2);
    paintGeo.translate(depth / 2, 0, 0);
    paintGeo.computeVertexNormals();
    return paintGeo;
  }, []);

  useEffect(() => {
    return () => {
      paint.dispose();
    };
  }, [paint]);

  return (
    <group>
      <mesh geometry={paint} castShadow receiveShadow material={mats.paint} />
      <mesh position={[0, 0.5, CAB_Z + 0.08]} receiveShadow material={mats.cladding}>
        <boxGeometry args={[BODY_W + 0.05, 0.24, CAB_LEN * 0.78]} />
      </mesh>
    </group>
  );
}

export function SportTrac({ paintId }: { paintId: PaintId }) {
  const mats = useMats(paintId);
  const glassH = ROOF - 0.1 - BELT;

  return (
    <group name="sport-trac-2004">
      <Wheel x={TRACK_F / 2} z={FRONT_AXLE_Z} mats={mats} />
      <Wheel x={-TRACK_F / 2} z={FRONT_AXLE_Z} mats={mats} />
      <Wheel x={TRACK_R / 2} z={REAR_AXLE_Z} mats={mats} />
      <Wheel x={-TRACK_R / 2} z={REAR_AXLE_Z} mats={mats} />

      <Hull mats={mats} />

      {/* cabin interior so glass reads as glass */}
      <mesh position={[0, 0.92, CAB_Z]} material={mats.interior}>
        <boxGeometry args={[BODY_W - 0.28, 0.7, CAB_LEN - 0.45]} />
      </mesh>
      <mesh
        position={[0, 1.14, COWL_Z - 0.15]}
        rotation={[-0.42, 0, 0]}
        material={mats.dark}
      >
        <boxGeometry args={[0.92, 0.08, 0.4]} />
      </mesh>

      {/* windshield — Explorer rake */}
      <mesh
        position={[0, BELT + glassH * 0.48, COWL_Z - 0.12]}
        rotation={[-0.58, 0, 0]}
        castShadow
        material={mats.glass}
      >
        <boxGeometry args={[BODY_W - 0.34, glassH * 0.95, 0.035]} />
      </mesh>

      {/* side glass — crew cab, not Hummer slits */}
      {[-1, 1].map((s) => (
        <group key={`glass${s}`}>
          <mesh
            position={[(BODY_W / 2 - 0.05) * s, BELT + glassH * 0.48, COWL_Z - 0.72]}
            material={mats.glass}
          >
            <boxGeometry args={[0.035, glassH * 0.9, 0.88]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 - 0.05) * s, BELT + glassH * 0.48, CAB_REAR_Z + 0.62]}
            material={mats.glass}
          >
            <boxGeometry args={[0.035, glassH * 0.9, 0.78]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 - 0.04) * s, BELT + glassH * 0.5, CAB_Z - 0.02]}
            material={mats.dark}
          >
            <boxGeometry args={[0.05, glassH, 0.1]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.015) * s, BELT + 0.02, COWL_Z - 0.7]}
            material={mats.chrome}
          >
            <boxGeometry args={[0.03, 0.035, 0.14]} />
          </mesh>
          <mesh
            position={[(BODY_W / 2 + 0.015) * s, BELT + 0.02, CAB_REAR_Z + 0.58]}
            material={mats.chrome}
          >
            <boxGeometry args={[0.03, 0.035, 0.14]} />
          </mesh>
        </group>
      ))}

      <mesh position={[0, BELT + glassH * 0.46, CAB_REAR_Z + 0.06]} material={mats.glass}>
        <boxGeometry args={[BODY_W - 0.36, glassH * 0.82, 0.035]} />
      </mesh>

      {/* A-pillars */}
      {[-1, 1].map((s) => (
        <mesh
          key={`ap${s}`}
          position={[(BODY_W / 2 - 0.12) * s, BELT + glassH * 0.5, COWL_Z - 0.12]}
          rotation={[-0.55, 0, 0.07 * s]}
          castShadow
          material={mats.paint}
        >
          <boxGeometry args={[0.07, glassH + 0.08, 0.09]} />
        </mesh>
      ))}

      {/* roof + D-pillars — greenhouse is open, not a Hummer brick */}
      <RoundedBox
        args={[BODY_W - 0.22, 0.06, CAB_LEN - 0.58]}
        radius={0.025}
        smoothness={3}
        position={[0, ROOF, CAB_Z - 0.06]}
        castShadow
        material={mats.paint}
      />
      {[-1, 1].map((s) => (
        <mesh
          key={`dp${s}`}
          position={[(BODY_W / 2 - 0.09) * s, BELT + glassH * 0.52, CAB_REAR_Z + 0.14]}
          castShadow
          material={mats.paint}
        >
          <boxGeometry args={[0.1, glassH + 0.1, 0.2]} />
        </mesh>
      ))}

      {/* Explorer face */}
      <RoundedBox
        args={[0.78, 0.34, 0.07]}
        radius={0.03}
        smoothness={3}
        position={[0, 0.82, NOSE_Z + 0.01]}
        material={mats.dark}
      />
      <mesh position={[0, 0.82, NOSE_Z + 0.05]} material={mats.chrome}>
        <boxGeometry args={[0.82, 0.015, 0.03]} />
      </mesh>
      <mesh position={[0, 0.68, NOSE_Z + 0.05]} material={mats.chrome}>
        <boxGeometry args={[0.78, 0.015, 0.03]} />
      </mesh>
      <mesh position={[0, 0.96, NOSE_Z + 0.05]} material={mats.chrome}>
        <boxGeometry args={[0.74, 0.015, 0.03]} />
      </mesh>
      {[-0.18, 0.18].map((x) => (
        <mesh key={x} position={[x, 0.82, NOSE_Z + 0.045]} material={mats.chrome}>
          <boxGeometry args={[0.012, 0.28, 0.02]} />
        </mesh>
      ))}
      <mesh position={[0, 0.82, NOSE_Z + 0.055]} material={mats.oval}>
        <planeGeometry args={[0.28, 0.12]} />
      </mesh>

      {[-1, 1].map((s) => (
        <group key={`hl${s}`}>
          <RoundedBox
            args={[0.38, 0.18, 0.1]}
            radius={0.03}
            smoothness={4}
            position={[(BODY_W / 2 - 0.32) * s, 0.84, NOSE_Z - 0.01]}
            material={mats.lens}
          />
          <mesh
            position={[(BODY_W / 2 - 0.18) * s, 0.84, NOSE_Z + 0.04]}
            material={mats.amber}
          >
            <boxGeometry args={[0.08, 0.14, 0.04]} />
          </mesh>
        </group>
      ))}

      {[-1, 1].map((s) => (
        <mesh
          key={`fog${s}`}
          position={[0.42 * s, 0.46, NOSE_Z + 0.08]}
          rotation={[Math.PI / 2, 0, 0]}
          material={mats.lens}
        >
          <cylinderGeometry args={[0.055, 0.055, 0.04, 16]} />
        </mesh>
      ))}

      {/* short composite bed */}
      <mesh position={[0, LOAD_H, BED_Z]} receiveShadow castShadow material={mats.cladding}>
        <boxGeometry args={[BED_IN_W + 0.06, 0.035, BED_LEN - 0.04]} />
      </mesh>
      {[-1, 1].map((s) => (
        <group key={`bed${s}`}>
          <RoundedBox
            args={[0.08, BED_IN_H, BED_LEN - 0.02]}
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
            <boxGeometry args={[0.1, 0.05, BED_LEN]} />
          </mesh>
        </group>
      ))}
      <RoundedBox
        args={[BED_IN_W + 0.12, BED_IN_H - 0.02, 0.08]}
        radius={0.02}
        smoothness={2}
        position={[0, LOAD_H + BED_IN_H / 2, TAIL_Z + 0.05]}
        castShadow
        material={mats.paint}
      />
      <mesh position={[0, BED_RAIL + 0.02, TAIL_Z + 0.05]} material={mats.cladding}>
        <boxGeometry args={[BED_IN_W + 0.14, 0.05, 0.1]} />
      </mesh>
      <mesh position={[0, LOAD_H + 0.22, TAIL_Z + 0.1]} material={mats.chrome}>
        <boxGeometry args={[0.3, 0.04, 0.03]} />
      </mesh>
      <mesh position={[0, 0.72, TAIL_Z + 0.1]} material={mats.oval}>
        <planeGeometry args={[0.22, 0.1]} />
      </mesh>

      {[-1, 1].map((s) => (
        <RoundedBox
          key={`tl${s}`}
          args={[0.09, 0.34, 0.14]}
          radius={0.02}
          smoothness={2}
          position={[(BED_IN_W / 2 + 0.01) * s, LOAD_H + 0.3, TAIL_Z + 0.08]}
          material={mats.red}
        />
      ))}

      <mesh position={[0, ROOF - 0.06, CAB_REAR_Z - 0.01]} material={mats.red}>
        <boxGeometry args={[0.55, 0.035, 0.04]} />
      </mesh>

      <RoundedBox
        args={[BODY_W - 0.18, 0.16, 0.16]}
        radius={0.03}
        smoothness={3}
        position={[0, 0.46, TAIL_Z - 0.04]}
        castShadow
        material={mats.paint}
      />
      <mesh position={[0, 0.4, TAIL_Z - 0.1]} material={mats.dark}>
        <boxGeometry args={[0.28, 0.07, 0.12]} />
      </mesh>
      <mesh position={[0, 0.56, TAIL_Z - 0.12]} material={mats.plate}>
        <boxGeometry args={[0.32, 0.16, 0.02]} />
      </mesh>

      {/* roof rails */}
      {[-1, 1].map((s) => (
        <group key={`rr${s}`}>
          <mesh
            position={[(BODY_W / 2 - 0.28) * s, ROOF + 0.04, CAB_Z + 0.05]}
            castShadow
            material={mats.dark}
          >
            <boxGeometry args={[0.028, 0.022, CAB_LEN - 0.7]} />
          </mesh>
          {[0.55, -0.55].map((z) => (
            <mesh
              key={z}
              position={[(BODY_W / 2 - 0.28) * s, ROOF + 0.015, CAB_Z + z]}
              material={mats.dark}
            >
              <boxGeometry args={[0.04, 0.05, 0.05]} />
            </mesh>
          ))}
        </group>
      ))}

      {/* mirrors */}
      {[-1, 1].map((s) => (
        <group key={`m${s}`} position={[(BODY_W / 2) * s, 1.16, COWL_Z - 0.08]}>
          <mesh position={[0.1 * s, 0, 0]} material={mats.paint}>
            <boxGeometry args={[0.18, 0.12, 0.2]} />
          </mesh>
          <mesh position={[0.12 * s, 0, 0.02]} material={mats.chrome}>
            <boxGeometry args={[0.02, 0.09, 0.15]} />
          </mesh>
        </group>
      ))}

      {/* running boards */}
      {[-1, 1].map((s) => (
        <mesh
          key={`rb${s}`}
          position={[(BODY_W / 2 + 0.04) * s, 0.34, CAB_Z + 0.05]}
          castShadow
          material={mats.dark}
        >
          <boxGeometry args={[0.14, 0.035, CAB_LEN * 0.62]} />
        </mesh>
      ))}

      {/* wipers */}
      <mesh position={[0.16, 1.14, COWL_Z + 0.06]} rotation={[0, 0, -0.18]} material={mats.dark}>
        <boxGeometry args={[0.58, 0.012, 0.018]} />
      </mesh>
      <mesh position={[-0.14, 1.13, COWL_Z + 0.04]} rotation={[0, 0, 0.22]} material={mats.dark}>
        <boxGeometry args={[0.5, 0.012, 0.018]} />
      </mesh>

      {/* antenna */}
      <mesh position={[-BODY_W / 2 + 0.14, 1.42, COWL_Z - 0.2]} material={mats.dark}>
        <cylinderGeometry args={[0.006, 0.004, 0.62, 8]} />
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

  return (
    <group>
      <mesh
        position={[0.42, 0.58, 1.52]}
        castShadow
        onClick={(e) => {
          e.stopPropagation();
          setJob("ac-compressor");
        }}
      >
        <boxGeometry args={[0.18, 0.16, 0.22]} />
        <meshStandardMaterial
          color="#2a3034"
          metalness={0.55}
          roughness={0.4}
          emissive={jobId?.startsWith("ac") || jobId === "heater-core" ? "#9aa3a7" : "#000000"}
          emissiveIntensity={jobId?.startsWith("ac") || jobId === "heater-core" ? 0.35 : 0}
        />
      </mesh>
      <mesh position={[0.42, 0.58, 1.64]} rotation={[Math.PI / 2, 0, 0]}>
        <cylinderGeometry args={[0.08, 0.08, 0.05, 20]} />
        <meshStandardMaterial color="#1a1c1e" metalness={0.3} roughness={0.55} />
      </mesh>
      {job ? (
        <mesh position={mark}>
          <sphereGeometry args={[0.035, 16, 16]} />
          <meshStandardMaterial
            color="#e8e4d8"
            emissive="#e8e4d8"
            emissiveIntensity={0.8}
          />
        </mesh>
      ) : null}
    </group>
  );
}
