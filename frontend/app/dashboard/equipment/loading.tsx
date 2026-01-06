import { Skeleton } from '@/components/ui/skeleton';

export default function LoadingEquipment() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-12 w-full max-w-md" />
      
      <div className="flex items-center gap-4">
        <Skeleton className="h-10 w-full max-w-xs" />
        <Skeleton className="h-10 w-32" />
        <Skeleton className="h-10 w-32" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[...Array(6)].map((_, i) => (
          <Skeleton key={i} className="h-64 w-full" />
        ))}
      </div>
    </div>
  );
}
