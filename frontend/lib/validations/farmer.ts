import * as z from 'zod';

/**
 * Farmer Validation Schemas
 */

export const farmerFormSchema = z.object({
  name: z.string()
    .min(2, 'Name must be at least 2 characters')
    .max(100, 'Name must not exceed 100 characters'),
  email: z.string()
    .email('Invalid email address')
    .min(1, 'Email is required')
    .max(150, 'Email must not exceed 150 characters'),
  phone: z.string()
    .min(10, 'Phone number must be at least 10 digits')
    .regex(/^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{1,9}$/, 'Invalid phone number format'),
  role: z.enum(['OWNER', 'MANAGER', 'WORKER'], {
    required_error: 'Role is required',
  }),
});

export const farmFormSchema = z.object({
  name: z.string()
    .min(2, 'Farm name must be at least 2 characters')
    .max(100, 'Farm name must not exceed 100 characters'),
  location: z.string()
    .min(3, 'Location must be at least 3 characters')
    .max(200, 'Location must not exceed 200 characters'),
  size: z.number()
    .min(0.1, 'Farm size must be greater than 0')
    .max(999999, 'Farm size is too large'),
});

export const farmerFilterSchema = z.object({
  search: z.string().optional(),
  active: z.enum(['all', 'active', 'inactive']).optional(),
  sortBy: z.enum(['name', 'email', 'registrationDate']).optional(),
  sortOrder: z.enum(['asc', 'desc']).optional(),
});

export type FarmerFormValues = z.infer<typeof farmerFormSchema>;
export type FarmFormValues = z.infer<typeof farmFormSchema>;
export type FarmerFilterValues = z.infer<typeof farmerFilterSchema>;
