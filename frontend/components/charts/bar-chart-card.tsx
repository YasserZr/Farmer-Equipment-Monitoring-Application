'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

interface BarChartData {
  name: string;
  [key: string]: string | number;
}

interface BarChartCardProps {
  title: string;
  description?: string;
  data: BarChartData[];
  dataKeys: Array<{
    key: string;
    color: string;
    name: string;
  }>;
  xAxisKey?: string;
  isLoading?: boolean;
  height?: number;
}

export function BarChartCard({
  title,
  description,
  data,
  dataKeys,
  xAxisKey = 'name',
  isLoading,
  height = 300,
}: BarChartCardProps) {
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
            <BarChart data={data}>
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
              {dataKeys.map((item) => (
                <Bar
                  key={item.key}
                  dataKey={item.key}
                  fill={item.color}
                  name={item.name}
                  radius={[4, 4, 0, 0]}
                />
              ))}
            </BarChart>
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
