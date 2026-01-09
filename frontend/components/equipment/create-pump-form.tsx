'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useCreatePump } from '@/hooks/use-equipment';
import { useFarmers } from '@/hooks/use-farmers';
import { EquipmentStatus } from '@/types/equipment';
import { toast } from 'sonner';

const pumpSchema = z.object({
  farmId: z.string().min(1, 'Farm is required'),
  model: z.string().min(2, 'Model must be at least 2 characters'),
  status: z.nativeEnum(EquipmentStatus),
  maxFlow: z.number().min(0.1, 'Max flow must be greater than 0'),
  location: z.string().min(3, 'Location must be at least 3 characters'),
  installationDate: z.string().min(1, 'Installation date is required'),
});

type PumpFormValues = z.infer<typeof pumpSchema>;

interface CreatePumpFormProps {
  onSuccess?: () => void;
}

export function CreatePumpForm({ onSuccess }: CreatePumpFormProps) {
  const { data: farmersData } = useFarmers(0, 100);
  const createPump = useCreatePump();

  const form = useForm<PumpFormValues>({
    resolver: zodResolver(pumpSchema),
    defaultValues: {
      farmId: '',
      model: '',
      status: EquipmentStatus.ACTIVE,
      maxFlow: 100,
      location: '',
      installationDate: new Date().toISOString().split('T')[0],
    },
  });

  const onSubmit = async (data: PumpFormValues) => {
    try {
      await createPump.mutateAsync(data);
      form.reset();
      onSuccess?.();
    } catch (error) {
      // Error handled by mutation hook
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="farmId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Farm *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select a farm" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {farmersData?.content?.map((farmer) => (
                    <SelectItem key={farmer.id} value={farmer.id}>
                      {farmer.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormDescription>Select the farm where the pump will be installed</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="model"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Model *</FormLabel>
              <FormControl>
                <Input placeholder="e.g., AgriPump 3000" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Status *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value={EquipmentStatus.ACTIVE}>Active</SelectItem>
                  <SelectItem value={EquipmentStatus.INACTIVE}>Inactive</SelectItem>
                  <SelectItem value={EquipmentStatus.MAINTENANCE}>Maintenance</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="maxFlow"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Max Flow (L/min) *</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  step="0.1"
                  min="0.1"
                  placeholder="100"
                  {...field}
                  onChange={(e) => field.onChange(parseFloat(e.target.value))}
                />
              </FormControl>
              <FormDescription>Maximum flow rate in liters per minute</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="location"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Location *</FormLabel>
              <FormControl>
                <Input placeholder="e.g., Field A, North Section" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="installationDate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Installation Date *</FormLabel>
              <FormControl>
                <Input type="date" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-3">
          <Button
            type="button"
            variant="outline"
            onClick={() => form.reset()}
            disabled={createPump.isPending}
          >
            Reset
          </Button>
          <Button type="submit" disabled={createPump.isPending || !form.formState.isValid}>
            {createPump.isPending ? 'Creating...' : 'Create Pump'}
          </Button>
        </div>
      </form>
    </Form>
  );
}
