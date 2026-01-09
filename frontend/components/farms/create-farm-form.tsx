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
import { useFarmers } from '@/hooks/use-farmers';

const farmSchema = z.object({
  farmerId: z.string().min(1, 'Farmer is required'),
  name: z.string().min(2, 'Farm name must be at least 2 characters'),
  location: z.string().min(3, 'Location must be at least 3 characters'),
  size: z.number().min(0.1, 'Size must be greater than 0'),
});

type FarmFormValues = z.infer<typeof farmSchema>;

interface CreateFarmFormProps {
  onSubmit: (data: FarmFormValues) => Promise<void>;
  isLoading?: boolean;
}

export function CreateFarmForm({ onSubmit, isLoading }: CreateFarmFormProps) {
  const { data: farmersData } = useFarmers(0, 100);

  const form = useForm<FarmFormValues>({
    resolver: zodResolver(farmSchema),
    defaultValues: {
      farmerId: '',
      name: '',
      location: '',
      size: 0,
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="farmerId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Farm Owner *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select a farmer" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {farmersData?.content?.map((farmer) => (
                    <SelectItem key={farmer.id} value={farmer.id}>
                      {farmer.name} ({farmer.email})
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormDescription>Select the owner of this farm</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Farm Name *</FormLabel>
              <FormControl>
                <Input placeholder="e.g., Green Valley Farm" {...field} />
              </FormControl>
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
                <Input placeholder="e.g., Iowa, USA" {...field} />
              </FormControl>
              <FormDescription>City, state, or region</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="size"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Size (acres) *</FormLabel>
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
              <FormDescription>Total farm size in acres</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-3">
          <Button
            type="button"
            variant="outline"
            onClick={() => form.reset()}
            disabled={isLoading}
          >
            Reset
          </Button>
          <Button type="submit" disabled={isLoading || !form.formState.isValid}>
            {isLoading ? 'Creating...' : 'Create Farm'}
          </Button>
        </div>
      </form>
    </Form>
  );
}
