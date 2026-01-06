'use client';

import { Battery, BatteryLow, BatteryWarning, BatteryFull } from 'lucide-react';
import { Progress } from '@/components/ui/progress';
import { cn } from '@/lib/utils';

interface BatteryIndicatorProps {
  level: number;
  status?: 'GOOD' | 'LOW' | 'CRITICAL';
  showPercentage?: boolean;
  size?: 'sm' | 'md' | 'lg';
  showIcon?: boolean;
}

export function BatteryIndicator({
  level,
  status,
  showPercentage = true,
  size = 'md',
  showIcon = true,
}: BatteryIndicatorProps) {
  const getColor = () => {
    if (status === 'CRITICAL' || level < 20) return 'text-red-600';
    if (status === 'LOW' || level < 50) return 'text-yellow-600';
    return 'text-green-600';
  };

  const getProgressColor = () => {
    if (status === 'CRITICAL' || level < 20) return 'bg-red-600';
    if (status === 'LOW' || level < 50) return 'bg-yellow-600';
    return 'bg-green-600';
  };

  const getBatteryIcon = () => {
    if (level < 20) return BatteryWarning;
    if (level < 50) return BatteryLow;
    if (level < 80) return Battery;
    return BatteryFull;
  };

  const Icon = getBatteryIcon();

  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-5 h-5',
    lg: 'w-6 h-6',
  };

  return (
    <div className="flex items-center gap-2">
      {showIcon && <Icon className={cn(sizeClasses[size], getColor())} />}
      <div className="flex-1 min-w-[60px]">
        <Progress value={level} className="h-2" indicatorClassName={getProgressColor()} />
      </div>
      {showPercentage && (
        <span className={cn('text-sm font-medium', getColor())}>{level}%</span>
      )}
    </div>
  );
}
