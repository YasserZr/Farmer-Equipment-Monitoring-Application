# Farmer Management Module

Comprehensive farmer management system with modern UI components, server-side rendering, and full CRUD operations.

## 📁 File Structure

```
app/dashboard/farmers/
├── page.tsx                    # Main farmers list page (Client Component)
├── new/
│   └── page.tsx               # Create new farmer page (Client Component)
├── [id]/
│   ├── page.tsx               # Farmer detail page (Server Component)
│   ├── edit/
│   │   └── page.tsx           # Edit farmer page (Client Component)
│   └── not-found.tsx          # 404 page for farmer not found
├── loading.tsx                # Loading state skeleton
└── not-found.tsx              # 404 page for farmers list

components/farmers/
├── farmer-form.tsx            # Reusable farmer form with validation
├── farmer-card.tsx            # Farmer card component for grid view
├── farmers-data-table.tsx     # Data table with pagination
├── delete-farmer-dialog.tsx   # Confirmation dialog for deletion
└── farmer-filters.tsx         # Advanced filtering component

lib/validations/
└── farmer.ts                  # Zod validation schemas
```

## 🎨 Features

### 1. Farmers List Page (`/dashboard/farmers`)
- **View Modes**: Toggle between table and grid view
- **Search**: Real-time search functionality
- **Pagination**: Navigate through pages of farmers
- **Actions**: View, Edit, Delete buttons for each farmer
- **Loading States**: Skeleton loaders during data fetch
- **Error Handling**: User-friendly error messages

### 2. Create Farmer Page (`/dashboard/farmers/new`)
- **Form Validation**: Zod schema validation with React Hook Form
- **Field Validation**:
  - First/Last Name: 2-50 characters
  - Email: Valid email format
  - Phone: Minimum 10 digits with format validation
  - Address: 5-200 characters
  - Date of Birth: Must be 18-120 years old
- **Loading States**: Disabled button during submission
- **Toast Notifications**: Success/error messages
- **Navigation**: Back button to farmers list

### 3. Farmer Detail Page (`/dashboard/farmers/[id]`)
- **Server Component**: Pre-rendered on server for better SEO
- **Complete Information**: All farmer details displayed
- **Statistics Card**: Farm count and account status
- **Farms List**: Displays all farms owned by farmer
- **Actions**: Edit button, back navigation
- **Responsive Design**: Mobile-optimized layout

### 4. Edit Farmer Page (`/dashboard/farmers/[id]/edit`)
- **Pre-filled Form**: Loads existing farmer data
- **Same Validation**: Uses same Zod schema as create
- **Loading States**: Skeleton while fetching data
- **Error Handling**: Redirects if farmer not found
- **Navigation**: Back to detail page

## 🔧 Components

### FarmerForm
Reusable form component with React Hook Form and Zod validation.

**Props:**
- `farmer?: Farmer` - Optional farmer data for editing
- `onSubmit: (data: FarmerFormValues) => Promise<void>` - Submit handler
- `isLoading?: boolean` - Loading state

**Features:**
- Controlled inputs with validation
- Real-time error messages
- Responsive 2-column layout on desktop
- Textarea for address field
- Date picker for date of birth

### FarmerCard
Card component for grid view display.

**Props:**
- `farmer: Farmer` - Farmer data
- `onDelete?: (id: string) => void` - Optional delete handler

**Features:**
- Avatar with initials
- Contact information (email, phone, address)
- Status badge (Active/Inactive)
- Action buttons (View, Edit, Delete)
- Hover effects

### FarmersDataTable
Table component with pagination and actions.

**Props:**
- `data: PaginatedResponse<Farmer>` - Paginated farmer data
- `onDelete?: (id: string) => void` - Optional delete handler
- `onPageChange?: (page: number) => void` - Page change handler

**Features:**
- Sortable columns
- Avatar display
- Status badges
- Action buttons
- Pagination controls
- Empty state handling

### DeleteFarmerDialog
Confirmation dialog for farmer deletion.

**Props:**
- `open: boolean` - Dialog open state
- `onOpenChange: (open: boolean) => void` - State change handler
- `onConfirm: () => void` - Confirmation handler
- `farmerName?: string` - Farmer name for display

### FarmerFilters
Advanced filtering dialog component.

**Props:**
- `onApply: (filters) => void` - Apply filters handler

**Features:**
- Search input
- Status filter (All/Active/Inactive)
- Sort by (Name, Email, Registration Date)
- Sort order (Ascending/Descending)
- Reset filters button

## 📝 Validation Schemas

### farmerFormSchema
```typescript
{
  firstName: string (2-50 chars)
  lastName: string (2-50 chars)
  email: string (valid email)
  phoneNumber: string (min 10 digits, phone format)
  address: string (5-200 chars)
  dateOfBirth: string (18-120 years old)
  active: boolean (optional, default: true)
}
```

### farmFormSchema
```typescript
{
  name: string (2-100 chars)
  location: string (3-200 chars)
  size: number (0.1-999999)
}
```

### farmerFilterSchema
```typescript
{
  search?: string
  active?: 'all' | 'active' | 'inactive'
  sortBy?: 'name' | 'email' | 'registrationDate'
  sortOrder?: 'asc' | 'desc'
}
```

## 🎯 Custom Hooks

All hooks are located in `/hooks/use-farmers.ts`:

- `useFarmers(page, size)` - Fetch paginated farmers
- `useFarmer(id)` - Fetch single farmer
- `useCreateFarmer()` - Create new farmer
- `useUpdateFarmer()` - Update farmer
- `useDeleteFarmer()` - Delete farmer
- `useFarms(farmerId, page, size)` - Fetch farmer's farms
- `useFarmerStatistics()` - Fetch farmer statistics

All hooks include:
- React Query integration
- Automatic cache invalidation
- Toast notifications
- Error handling

## 🎨 Styling

- **shadcn/ui**: Pre-built accessible components
- **Tailwind CSS**: Utility-first styling
- **Responsive Design**: Mobile, tablet, desktop layouts
- **Dark Mode Ready**: Uses CSS variables
- **Animations**: Smooth transitions and hover effects

## 🚀 Usage Examples

### Create a new farmer:
1. Navigate to `/dashboard/farmers`
2. Click "Add Farmer" button
3. Fill in the form
4. Click "Create Farmer"

### Edit a farmer:
1. From farmers list, click Edit icon
2. Update fields
3. Click "Update Farmer"

### Delete a farmer:
1. From farmers list, click Delete icon
2. Confirm deletion in dialog

### View farmer details:
1. Click on farmer row or "View" button
2. See complete information and farms list

## 📱 Responsive Breakpoints

- **Mobile**: < 768px - Single column, stacked layout
- **Tablet**: 768px - 1024px - 2 columns
- **Desktop**: > 1024px - 3 columns, full table view

## ♿ Accessibility

- **Keyboard Navigation**: Full keyboard support
- **ARIA Labels**: Proper labeling for screen readers
- **Focus Management**: Clear focus indicators
- **Form Validation**: Clear error messages
- **Color Contrast**: WCAG AA compliant

## 🔄 State Management

- **React Query**: Server state and caching
- **React Hook Form**: Form state management
- **Zustand**: Global auth state (from parent app)

## 🐛 Error Handling

- **Network Errors**: Toast notifications
- **Validation Errors**: Inline form errors
- **404 Errors**: Not found page
- **Loading States**: Skeletons and spinners

## 📊 Performance

- **Server Components**: Pre-rendered farmer detail page
- **Code Splitting**: Automatic route-based splitting
- **React Query Caching**: Reduces API calls
- **Debounced Search**: Optimized search input
- **Lazy Loading**: Images and heavy components

## 🔐 Security

- **Input Validation**: Zod schema validation
- **XSS Protection**: React automatic escaping
- **Auth Headers**: Automatic token injection
- **CSRF Protection**: API client configuration
