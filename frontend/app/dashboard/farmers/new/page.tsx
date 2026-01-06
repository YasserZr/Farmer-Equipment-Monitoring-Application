'use client';

import { useRouter } from 'next/navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { FarmerForm } from '@/components/farmers/farmer-form';
import { useCreateFarmer } from '@/hooks/use-farmers';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import type { FarmerFormValues } from '@/lib/validations/farmer';

export default function NewFarmerPage() {
  const router = useRouter();
  const createFarmer = useCreateFarmer();

  const handleSubmit = async (data: FarmerFormValues) => {
    try {
      await createFarmer.mutateAsync(data);
      router.push('/dashboard/farmers');
    } catch (error) {
      // Error handled by hook
    }
  };

  return (
    <div className="space-y-6 max-w-4xl">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/dashboard/farmers">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Farmers
          </Link>
        </Button>
      </div>

      {/* Form Card */}
      <Card>
        <CardHeader>
          <CardTitle>Create New Farmer</CardTitle>
          <CardDescription>
            Add a new farmer to the system. All fields marked with * are required.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <FarmerForm onSubmit={handleSubmit} isLoading={createFarmer.isPending} />
        </CardContent>
      </Card>
    </div>
  );
}
