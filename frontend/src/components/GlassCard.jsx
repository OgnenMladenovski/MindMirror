import { Card } from '@mui/material';
import { motion } from 'framer-motion';

const MotionCard = motion.create(Card);

/** Rounded translucent card with a subtle entrance animation. */
export default function GlassCard({ children, sx, delay = 0, ...props }) {
  return (
    <MotionCard
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay, ease: 'easeOut' }}
      elevation={0}
      sx={{ p: { xs: 2, sm: 2.5 }, height: '100%', ...sx }}
      {...props}
    >
      {children}
    </MotionCard>
  );
}
