'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface DonutChartData {
  name: string;
  value: number;
  color?: string;
}

interface DonutChartCardProps {
  title: string;
  description?: string;
  data: DonutChartData[];
  colors?: string[];
  isLoading?: boolean;
  height?: number;
  showPercentage?: boolean;
}

const DEFAULT_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export function DonutChartCard({
  title,
  description,
  data,
  colors = DEFAULT_COLORS,
  isLoading,
  height = 300,
  showPercentage = true,
}: DonutChartCardProps) {
  const total = data.reduce((sum, item) => sum + item.value, 0);

  const renderLabel = (entry: any) => {
    if (!showPercentage) return entry.name;
    const percent = ((entry.value / total) * 100).toFixed(0);
    return `${entry.name}: ${percent}%`;
  };

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
            <PieChart>
              <Pie
                data={data}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={renderLabel}
                outerRadius={100}
                innerRadius={60}
                fill="#8884d8"
                dataKey="value"
                paddingAngle={2}
              >
                {data.map((entry, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={entry.color || colors[index % colors.length]}
                  />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: 'hsl(var(--background))',
                  border: '1px solid hsl(var(--border))',
                  borderRadius: '6px',
                }}
              />
              <Legend />
            </PieChart>
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
