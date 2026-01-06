import * as z from 'zod';

/**
 * Farmer Validation Schemas
 */

export const farmerFormSchema = z.object({
  firstName: z.string()
    .min(2, 'First name must be at least 2 characters')
    .max(50, 'First name must not exceed 50 characters'),
  lastName: z.string()
    .min(2, 'Last name must be at least 2 characters')
    .max(50, 'Last name must not exceed 50 characters'),
  email: z.string()
    .email('Invalid email address')
    .min(1, 'Email is required'),
  phoneNumber: z.string()
    .min(10, 'Phone number must be at least 10 digits')
    .regex(/^[0-9+\-\s()]+$/, 'Invalid phone number format'),
  address: z.string()
    .min(5, 'Address must be at least 5 characters')
    .max(200, 'Address must not exceed 200 characters'),
  dateOfBirth: z.string()
    .min(1, 'Date of birth is required')
    .refine((date) => {
      const birthDate = new Date(date);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();
      return age >= 18 && age <= 120;
    }, 'Farmer must be between 18 and 120 years old'),
  active: z.boolean().optional().default(true),
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
