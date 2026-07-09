import { motion } from 'framer-motion';

/**
 * The digital-twin avatar. Its face, colour and animation change with the
 * wellbeing state (EXCELLENT / HAPPY / NEUTRAL / STRESSED / BURNED_OUT / EXHAUSTED).
 */
export default function AvatarView({ state = 'NEUTRAL', attributes = {}, size = 220 }) {
  // Avatar body always uses the brand apricot (matches the numbered step badges).
  const color = '#f6a24b';
  const smile = clamp(num(attributes.smile, 0.5));
  const glow = attributes.glow === true || state === 'EXCELLENT' || state === 'HAPPY';
  const darkCircles = attributes.dark_circles === true;
  const sleeping = state === 'EXHAUSTED';
  const crying = state === 'BURNED_OUT';
  const stressed = state === 'STRESSED' || state === 'BURNED_OUT';

  // Mouth curvature: high smile = upward arc, low = frown.
  const ctrlY = 128 + (smile - 0.42) * 70;
  const mouth = `M72,122 Q100,${ctrlY} 128,122`;

  const breathe = { scale: [1, 1.035, 1], transition: { duration: 3.4, repeat: Infinity, ease: 'easeInOut' } };
  const bob = { y: [0, -6, 0], transition: { duration: 3.4, repeat: Infinity, ease: 'easeInOut' } };

  return (
    <motion.div animate={bob} style={{ width: size, height: size }}>
      <svg viewBox="0 0 200 200" width={size} height={size}>
        <defs>
          <radialGradient id="face" cx="50%" cy="40%" r="65%">
            <stop offset="0%" stopColor={lighten(color)} />
            <stop offset="100%" stopColor={color} />
          </radialGradient>
          <filter id="soft" x="-40%" y="-40%" width="180%" height="180%">
            <feGaussianBlur stdDeviation="6" />
          </filter>
        </defs>

        {glow && (
          <motion.circle
            cx="100" cy="100" r="82" fill={color} opacity="0.35" filter="url(#soft)"
            animate={{ opacity: [0.2, 0.45, 0.2], scale: [1, 1.06, 1] }}
            transition={{ duration: 2.6, repeat: Infinity, ease: 'easeInOut' }}
            style={{ transformOrigin: '100px 100px' }}
          />
        )}

        <motion.g animate={breathe} style={{ transformOrigin: '100px 100px' }}>
          {/* head */}
          <circle cx="100" cy="100" r="66" fill="url(#face)" />
          <circle cx="100" cy="100" r="66" fill="none" stroke="rgba(0,0,0,0.12)" strokeWidth="2" />

          {/* dark circles */}
          {darkCircles && (
            <>
              <path d="M68,108 Q80,116 92,108" fill="none" stroke="rgba(40,20,60,0.35)" strokeWidth="4" strokeLinecap="round" />
              <path d="M108,108 Q120,116 132,108" fill="none" stroke="rgba(40,20,60,0.35)" strokeWidth="4" strokeLinecap="round" />
            </>
          )}

          {/* eyes */}
          {sleeping ? (
            <>
              <path d="M70,96 Q80,104 90,96" fill="none" stroke="#2b2440" strokeWidth="4" strokeLinecap="round" />
              <path d="M110,96 Q120,104 130,96" fill="none" stroke="#2b2440" strokeWidth="4" strokeLinecap="round" />
            </>
          ) : (
            <>
              <motion.circle cx="80" cy="95" r="7" fill="#2b2440"
                animate={{ scaleY: [1, 0.1, 1] }} transition={{ duration: 4.5, repeat: Infinity, times: [0, 0.05, 0.1] }}
                style={{ transformOrigin: '80px 95px' }} />
              <motion.circle cx="120" cy="95" r="7" fill="#2b2440"
                animate={{ scaleY: [1, 0.1, 1] }} transition={{ duration: 4.5, repeat: Infinity, times: [0, 0.05, 0.1] }}
                style={{ transformOrigin: '120px 95px' }} />
            </>
          )}

          {/* eyebrows when stressed */}
          {stressed && (
            <>
              <path d="M70,82 L92,88" stroke="#2b2440" strokeWidth="3.5" strokeLinecap="round" />
              <path d="M130,82 L108,88" stroke="#2b2440" strokeWidth="3.5" strokeLinecap="round" />
            </>
          )}

          {/* mouth */}
          <path d={mouth} fill="none" stroke="#2b2440" strokeWidth="4.5" strokeLinecap="round" />

          {/* cheeks when happy */}
          {(state === 'EXCELLENT' || state === 'HAPPY') && (
            <>
              <circle cx="72" cy="115" r="7" fill="#f9b4c6" opacity="0.5" />
              <circle cx="128" cy="115" r="7" fill="#f9b4c6" opacity="0.5" />
            </>
          )}

          {/* tear when crying */}
          {crying && (
            <motion.path
              d="M120,104 q4,10 0,16 q-4,-6 0,-16"
              fill="#6cc4e8"
              animate={{ y: [0, 18], opacity: [0.9, 0] }}
              transition={{ duration: 1.8, repeat: Infinity, ease: 'easeIn' }}
            />
          )}

          {/* sweat when stressed */}
          {stressed && !crying && (
            <motion.path
              d="M150,74 q4,10 0,16 q-4,-6 0,-16"
              fill="#6cc4e8"
              animate={{ y: [0, 14], opacity: [0.9, 0] }}
              transition={{ duration: 2, repeat: Infinity, ease: 'easeIn' }}
            />
          )}
        </motion.g>

        {/* sparkles when excellent */}
        {state === 'EXCELLENT' &&
          [[40, 60], [160, 55], [150, 140], [45, 135]].map(([x, y], i) => (
            <motion.text key={i} x={x} y={y} fontSize="16"
              animate={{ opacity: [0, 1, 0], scale: [0.6, 1.1, 0.6] }}
              transition={{ duration: 2.2, repeat: Infinity, delay: i * 0.4 }}>✨</motion.text>
          ))}

        {/* zzz when exhausted */}
        {sleeping &&
          [[150, 60, 12], [162, 46, 16], [174, 30, 20]].map(([x, y, s], i) => (
            <motion.text key={i} x={x} y={y} fontSize={s} fontWeight="700" fill="#a5abc9"
              animate={{ opacity: [0, 1, 0], y: [y, y - 10] }}
              transition={{ duration: 2.4, repeat: Infinity, delay: i * 0.5 }}>z</motion.text>
          ))}
      </svg>
    </motion.div>
  );
}

function num(v, d) { return typeof v === 'number' ? v : d; }
function clamp(v) { return Math.max(0, Math.min(1, v)); }
function lighten(hex) {
  const n = parseInt(hex.slice(1), 16);
  const r = Math.min(255, ((n >> 16) & 255) + 45);
  const g = Math.min(255, ((n >> 8) & 255) + 45);
  const b = Math.min(255, (n & 255) + 45);
  return `rgb(${r},${g},${b})`;
}
