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
import { useCreateSensor } from '@/hooks/use-equipment';
import { useFarmers } from '@/hooks/use-farmers';
import { SensorType } from '@/types/equipment';

const sensorSchema = z.object({
  farmId: z.string().min(1, 'Farm is required'),
  type: z.nativeEnum(SensorType),
  model: z.string().min(2, 'Model must be at least 2 characters'),
  battery: z.number().min(0).max(100, 'Battery must be between 0 and 100'),
  location: z.string().min(3, 'Location must be at least 3 characters'),
  installationDate: z.string().min(1, 'Installation date is required'),
  alertThreshold: z.number().min(0).max(100).optional(),
});

type SensorFormValues = z.infer<typeof sensorSchema>;

interface CreateSensorFormProps {
  onSuccess?: () => void;
}

export function CreateSensorForm({ onSuccess }: CreateSensorFormProps) {
  const { data: farmersData } = useFarmers(0, 100);
  const createSensor = useCreateSensor();

  const form = useForm<SensorFormValues>({
    resolver: zodResolver(sensorSchema),
    defaultValues: {
      farmId: '',
      type: SensorType.TEMPERATURE,
      model: '',
      battery: 100,
      location: '',
      installationDate: new Date().toISOString().split('T')[0],
      alertThreshold: 20,
    },
  });

  const onSubmit = async (data: SensorFormValues) => {
    try {
      await createSensor.mutateAsync(data);
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
              <FormDescription>Select the farm where the sensor will be installed</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="type"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Sensor Type *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select type" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value={SensorType.TEMPERATURE}>Temperature</SelectItem>
                  <SelectItem value={SensorType.HUMIDITY}>Humidity</SelectItem>
                  <SelectItem value={SensorType.SOIL_MOISTURE}>Soil Moisture</SelectItem>
                </SelectContent>
              </Select>
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
                <Input placeholder="e.g., TempSensor Pro 2000" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="battery"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Battery Level (%) *</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  min="0"
                  max="100"
                  placeholder="100"
                  {...field}
                  onChange={(e) => field.onChange(parseInt(e.target.value))}
                />
              </FormControl>
              <FormDescription>Current battery level percentage</FormDescription>
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
                <Input placeholder="e.g., Field B, East Corner" {...field} />
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

        <FormField
          control={form.control}
          name="alertThreshold"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Alert Threshold (Optional)</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  min="0"
                  max="100"
                  placeholder="20"
                  {...field}
                  onChange={(e) => field.onChange(parseInt(e.target.value) || undefined)}
                />
              </FormControl>
              <FormDescription>Battery level to trigger alerts</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-3">
          <Button
            type="button"
            variant="outline"
            onClick={() => form.reset()}
            disabled={createSensor.isPending}
          >
            Reset
          </Button>
          <Button type="submit" disabled={createSensor.isPending || !form.formState.isValid}>
            {createSensor.isPending ? 'Creating...' : 'Create Sensor'}
          </Button>
        </div>
      </form>
    </Form>
  );
}
