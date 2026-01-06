'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useFarmer, useUpdateFarmer } from '@/hooks/use-farmers';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { FarmerForm } from '@/components/farmers/farmer-form';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { ArrowLeft } from 'lucide-react';
import type { FarmerFormValues } from '@/lib/validations/farmer';

export default function EditFarmerPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { data: farmer, isLoading, isError } = useFarmer(params.id);
  const updateFarmer = useUpdateFarmer();

  const handleSubmit = async (data: FarmerFormValues) => {
    try {
      await updateFarmer.mutateAsync({ id: params.id, data });
      router.push(`/dashboard/farmers/${params.id}`);
    } catch (error) {
      // Error handled by hook
    }
  };

  if (isError) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-2">Failed to load farmer</p>
          <Button onClick={() => router.push('/dashboard/farmers')}>Back to Farmers</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-4xl">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" asChild>
          <Link href={`/dashboard/farmers/${params.id}`}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Farmer Details
          </Link>
        </Button>
      </div>

      {/* Form Card */}
      <Card>
        <CardHeader>
          <CardTitle>Edit Farmer</CardTitle>
          <CardDescription>
            Update farmer information. All fields marked with * are required.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {Array.from({ length: 5 }).map((_, i) => (
                  <div key={i} className="space-y-2">
                    <Skeleton className="h-4 w-24" />
                    <Skeleton className="h-10 w-full" />
                  </div>
                ))}
              </div>
              <Skeleton className="h-24 w-full" />
            </div>
          ) : farmer ? (
            <FarmerForm
              farmer={farmer}
              onSubmit={handleSubmit}
              isLoading={updateFarmer.isPending}
            />
          ) : null}
        </CardContent>
      </Card>
    </div>
  );
}
