'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

interface AreaChartData {
  name: string;
  [key: string]: string | number;
}

interface AreaChartCardProps {
  title: string;
  description?: string;
  data: AreaChartData[];
  dataKeys: Array<{
    key: string;
    color: string;
    name: string;
  }>;
  xAxisKey?: string;
  isLoading?: boolean;
  height?: number;
}

export function AreaChartCard({
  title,
  description,
  data,
  dataKeys,
  xAxisKey = 'name',
  isLoading,
  height = 300,
}: AreaChartCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        {description && <CardDescription>{description}</CardDescription>}
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <Skeleton className="w-full" style={{ height }} />
        ) : data && data.length > 0 ? (
          <ResponsiveContainer width="100%" height={height}>
            <AreaChart data={data}>
              <defs>
                {dataKeys.map((item, index) => (
                  <linearGradient key={item.key} id={`color${index}`} x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor={item.color} stopOpacity={0.8} />
                    <stop offset="95%" stopColor={item.color} stopOpacity={0.1} />
                  </linearGradient>
                ))}
              </defs>
              <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
              <XAxis dataKey={xAxisKey} className="text-xs" />
              <YAxis className="text-xs" />
              <Tooltip
                contentStyle={{
                  backgroundColor: 'hsl(var(--background))',
                  border: '1px solid hsl(var(--border))',
                  borderRadius: '6px',
                }}
              />
              <Legend />
              {dataKeys.map((item, index) => (
                <Area
                  key={item.key}
                  type="monotone"
                  dataKey={item.key}
                  stroke={item.color}
                  fill={`url(#color${index})`}
                  name={item.name}
                  strokeWidth={2}
                />
              ))}
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex items-center justify-center" style={{ height }}>
            <p className="text-muted-foreground text-sm">No data available</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
