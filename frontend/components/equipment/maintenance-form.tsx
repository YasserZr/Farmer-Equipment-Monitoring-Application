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
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Calendar } from 'lucide-react';

const maintenanceSchema = z.object({
  equipmentId: z.string().min(1, 'Equipment is required'),
  scheduledDate: z.string().min(1, 'Scheduled date is required'),
  description: z.string().min(10, 'Description must be at least 10 characters'),
  estimatedDuration: z.number().min(1, 'Duration must be at least 1 hour'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'URGENT']),
  recurrence: z.enum(['NONE', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY']),
  notes: z.string().optional(),
});

type MaintenanceFormValues = z.infer<typeof maintenanceSchema>;

interface MaintenanceFormProps {
  equipmentId?: string;
  onSubmit: (data: MaintenanceFormValues) => Promise<void>;
  isLoading?: boolean;
}

export function MaintenanceForm({ equipmentId, onSubmit, isLoading }: MaintenanceFormProps) {
  const form = useForm<MaintenanceFormValues>({
    resolver: zodResolver(maintenanceSchema),
    defaultValues: {
      equipmentId: equipmentId || '',
      scheduledDate: '',
      description: '',
      estimatedDuration: 2,
      priority: 'MEDIUM',
      recurrence: 'NONE',
      notes: '',
    },
  });

  const selectedRecurrence = form.watch('recurrence');

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="equipmentId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Equipment ID *</FormLabel>
              <FormControl>
                <Input placeholder="Enter equipment ID" {...field} disabled={!!equipmentId} />
              </FormControl>
              <FormDescription>ID of the equipment requiring maintenance</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="scheduledDate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Scheduled Date & Time *</FormLabel>
              <FormControl>
                <div className="relative">
                  <Input type="datetime-local" {...field} min={new Date().toISOString().slice(0, 16)} />
                  <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground pointer-events-none" />
                </div>
              </FormControl>
              <FormDescription>When should this maintenance be performed?</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="priority"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Priority *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select priority" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value="LOW">🟢 Low</SelectItem>
                  <SelectItem value="MEDIUM">🟡 Medium</SelectItem>
                  <SelectItem value="HIGH">🟠 High</SelectItem>
                  <SelectItem value="URGENT">🔴 Urgent</SelectItem>
                </SelectContent>
              </Select>
              <FormDescription>Set the priority level for this maintenance task</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="recurrence"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Recurrence *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select recurrence" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value="NONE">None (One-time)</SelectItem>
                  <SelectItem value="WEEKLY">Weekly</SelectItem>
                  <SelectItem value="MONTHLY">Monthly</SelectItem>
                  <SelectItem value="QUARTERLY">Quarterly (Every 3 months)</SelectItem>
                  <SelectItem value="YEARLY">Yearly</SelectItem>
                </SelectContent>
              </Select>
              <FormDescription>
                {selectedRecurrence !== 'NONE' 
                  ? `This maintenance will repeat ${selectedRecurrence.toLowerCase()}`
                  : 'This is a one-time maintenance task'}
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Description *</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Describe the maintenance work to be performed..."
                  className="resize-none"
                  rows={4}
                  {...field}
                />
              </FormControl>
              <FormDescription>Detailed description of the maintenance tasks</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="estimatedDuration"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Estimated Duration (hours) *</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  min={0.5}
                  step={0.5}
                  max={24}
                  {...field}
                  onChange={(e) => field.onChange(parseFloat(e.target.value))}
                />
              </FormControl>
              <FormDescription>Expected time to complete the maintenance</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="notes"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Additional Notes</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Any additional information, tools required, safety concerns..."
                  className="resize-none"
                  rows={3}
                  {...field}
                />
              </FormControl>
              <FormDescription>Optional additional details</FormDescription>
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
            {isLoading ? 'Scheduling...' : 'Schedule Maintenance'}
          </Button>
        </div>
      </form>
    </Form>
  );
}
