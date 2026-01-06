# Equipment Monitoring Module - Implementation Guide

## Overview

Complete equipment monitoring system with real-time updates via WebSocket. Includes equipment list with filtering, pump/sensor detail pages, maintenance scheduling, and reports with interactive charts.

## 📦 Components Created (15 Files)

### Core Pages (8 files)

1. **Equipment List Page** - `app/dashboard/equipment/page.tsx`
   - Two tabs: Pumps and Sensors with live counts
   - Search functionality by model/location
   - Status filter dropdown (All/Active/Inactive/Maintenance)
   - Real-time WebSocket connection status indicator
   - Paginated grid layout with equipment cards
   - Loading states and empty states
   
2. **Pump Detail Page** - `app/dashboard/equipment/pumps/[id]/page.tsx`
   - Equipment information with status badge
   - Status control dropdown (Active/Inactive/Maintenance)
   - Specifications: location, max flow, installation date
   - Maintenance information with overdue indicator
   - Schedule maintenance dialog
   - Quick stats sidebar
   
3. **Sensor Detail Page** - `app/dashboard/equipment/sensors/[id]/page.tsx`
   - Sensor information with online/offline status
   - Large battery indicator with progress bar
   - Battery status breakdown (Level/Status/Active)
   - Sensor specifications and last communication
   - Active alerts section (critical/low battery, offline)
   - Quick stats sidebar
   
4. **Maintenance Page** - `app/dashboard/equipment/maintenance/page.tsx`
   - Summary cards (Upcoming, Completed, Overdue, Total)
   - Two tabs: Upcoming Schedule and History
   - Maintenance table with equipment links
   - Overdue indicator with red highlighting
   - Schedule maintenance dialog
   - Filter functionality
   
5. **Reports Page** - `app/dashboard/equipment/reports/page.tsx`
   - Summary cards (Total, Active, Maintenance, Low Battery)
   - Four charts with Recharts:
     - Equipment Distribution (Pie chart)
     - Status Distribution (Bar chart)
     - Maintenance Trends (Line chart)
     - Battery Status Distribution (Bar chart)
   - Export PDF/CSV buttons
   
6. **Loading Pages** (3 files)
   - `app/dashboard/equipment/loading.tsx`
   - `app/dashboard/equipment/pumps/[id]/loading.tsx`
   - `app/dashboard/equipment/sensors/[id]/loading.tsx`

### Reusable Components (3 files)

1. **BatteryIndicator** - `components/equipment/battery-indicator.tsx`
   - Props: `level`, `status`, `showPercentage`, `size`, `showIcon`
   - Color-coded progress bar (Red <20%, Yellow 20-50%, Green >50%)
   - Battery icons based on level
   - Three sizes: sm, md, lg
   
2. **StatusBadge** - `components/equipment/status-badge.tsx`
   - Props: `status` (ACTIVE/INACTIVE/MAINTENANCE), `size`
   - Color mapping with borders and backgrounds
   - Consistent styling across the application
   
3. **MaintenanceForm** - `components/equipment/maintenance-form.tsx`
   - Zod validation schema
   - React Hook Form integration
   - Fields: scheduledDate, description, estimatedDuration, notes
   - Date-time picker with calendar icon

### Hooks & Utilities (4 files)

1. **useWebSocket** - `hooks/use-websocket.ts`
   - Connects to `ws://localhost:8080/ws-events`
   - Auto-reconnect on disconnect (5 second delay)
   - Invalidates React Query cache on equipment events
   - Returns: `{ isConnected, lastMessage, disconnect, reconnect }`
   - Fallback: `usePolling` hook for non-WebSocket environments
   
2. **Equipment Hooks** - `hooks/use-equipment.ts` (updated)
   - Added `useMaintenanceSchedule()` - Fetch maintenance schedule
   - Added `useEquipmentStatistics()` - Fetch statistics for reports
   
3. **Date Utilities** - `lib/utils.ts` (updated)
   - `formatDate(date)` - Returns "Jan 15, 2024"
   - `formatDateTime(date)` - Returns "Jan 15, 2024, 10:30 AM"
   - `formatRelativeTime(date)` - Returns "2 hours ago"

4. **shadcn/ui Components** (2 files)
   - `components/ui/progress.tsx` - Progress bar for battery indicator
   - `components/ui/tabs.tsx` - Tabs for equipment list and maintenance

## 🎨 Features

### Equipment List Page

- **Tabs Navigation**: Toggle between Pumps and Sensors
- **Search**: Real-time search by model or location
- **Status Filter**: Filter by equipment status
- **Real-time Indicator**: Live/Offline badge showing WebSocket connection
- **Pump Cards**:
  - Model, ID, location with map pin icon
  - Max flow rate
  - Operational status badge
  - Maintenance overdue warning
  - Link to detail page
- **Sensor Cards**:
  - Type, model, location
  - Battery indicator with progress bar
  - Online/offline status
  - Last communication timestamp
  - Low battery/critical/offline warnings
  - Link to detail page
- **Pagination**: Previous/Next buttons with page numbers
- **Loading States**: 6 skeleton cards
- **Empty States**: Icons with messages

### Pump Detail Page

- **Status Control**: Dropdown to change status (Active/Inactive/Maintenance)
- **Operational Badge**: Shows if pump is operational
- **Specifications**:
  - Location with map pin
  - Maximum flow rate
  - Installation date
  - Last maintenance date (if available)
- **Next Maintenance**: Shows scheduled date with overdue indicator
- **Schedule Maintenance**: Opens dialog with form
- **Quick Stats Sidebar**: Status, operational, farm ID, max flow, alerts

### Sensor Detail Page

- **Online Status**: Badge with WiFi icons
- **Battery Visualization**:
  - Large progress bar with color coding
  - Percentage display
  - Status text (GOOD/LOW/CRITICAL)
  - Breakdown: Level, Status, Active
- **Sensor Information**:
  - Location with map pin
  - Sensor type
  - Last communication with relative time
  - Alert threshold (if configured)
- **Active Alerts**:
  - Critical battery alert (red)
  - Low battery warning (yellow)
  - Offline status (gray)
- **Quick Stats Sidebar**: Status, online, farm ID, battery, threshold

### Maintenance Page

- **Summary Cards**: Upcoming, Completed, Overdue, Total records
- **Upcoming Schedule Tab**:
  - Table with columns: Equipment, Type, Scheduled Date, Description, Duration, Status
  - Red highlighting for overdue maintenance
  - Links to equipment detail pages
  - View buttons for each record
- **History Tab**:
  - Completed maintenance records
  - Includes completion date and notes
- **Schedule Maintenance**: Dialog with form
- **Filter**: Apply filters by equipment type, date range, status

### Reports Page

- **Summary Cards**: Total Equipment, Active, In Maintenance, Low Battery Sensors
- **Equipment Distribution Chart**: Pie chart showing Pumps vs Sensors
- **Status Distribution Chart**: Bar chart by status (Active/Inactive/Maintenance)
- **Maintenance Trends Chart**: Line chart showing scheduled vs completed over time
- **Battery Status Chart**: Bar chart showing battery levels by range
- **Export Options**: PDF and CSV export buttons

## 🔌 Real-time Updates

### WebSocket Integration

```typescript
// Usage in component
import { useWebSocket } from '@/hooks/use-websocket';

const { isConnected, lastMessage } = useWebSocket({ enabled: true });

// Display connection status
<Badge variant={isConnected ? 'default' : 'secondary'}>
  {isConnected ? 'Live' : 'Offline'}
</Badge>
```

### Event Types Handled

- `EQUIPMENT_CREATED`
- `EQUIPMENT_UPDATED`
- `EQUIPMENT_DELETED`
- `SENSOR_CREATED`
- `SENSOR_UPDATED`
- `MAINTENANCE_SCHEDULED`

### Cache Invalidation

When events are received, React Query cache is automatically invalidated:
- `['pumps']` - Refetches pump list
- `['sensors']` - Refetches sensor list
- `['equipment']` - Refetches equipment data

## 📊 Charts (Recharts)

All charts are responsive and use consistent theming:

```typescript
// Equipment Distribution (Pie)
<PieChart>
  <Pie dataKey="value" label={(entry) => `${entry.name}: ${entry.percent}%`} />
</PieChart>

// Status Distribution (Bar)
<BarChart data={statusDistribution}>
  <Bar dataKey="pumps" fill="#3b82f6" />
  <Bar dataKey="sensors" fill="#10b981" />
</BarChart>

// Maintenance Trends (Line)
<LineChart data={maintenanceTrends}>
  <Line dataKey="scheduled" stroke="#3b82f6" />
  <Line dataKey="completed" stroke="#10b981" />
</LineChart>
```

## 🎯 TypeScript Types

All components use strict TypeScript types from `types/equipment.ts`:

- `ConnectedPump` - Pump equipment type
- `ConnectedSensor` - Sensor equipment type
- `MaintenanceRequest` - Maintenance scheduling data
- `UpdatePumpRequest` - Pump update data
- `UpdateSensorRequest` - Sensor update data

## 🧭 Navigation

Updated dashboard navigation (`app/dashboard/layout.tsx`):

```typescript
const navigation = [
  { name: 'Dashboard', href: '/dashboard', icon: Home },
  { name: 'Farmers', href: '/dashboard/farmers', icon: Users },
  { name: 'Equipment', href: '/dashboard/equipment', icon: Activity },
  { name: 'Maintenance', href: '/dashboard/equipment/maintenance', icon: Wrench },
  { name: 'Reports', href: '/dashboard/equipment/reports', icon: BarChart3 },
  { name: 'Events', href: '/dashboard/events', icon: Bell },
];
```

## 🚀 Usage Examples

### View Equipment List

1. Navigate to `/dashboard/equipment`
2. Toggle between Pumps and Sensors tabs
3. Use search to find specific equipment
4. Filter by status (Active/Inactive/Maintenance)
5. Click "View Details" to see equipment detail page

### Monitor Pump Status

1. Go to equipment list
2. Click on a pump card
3. View pump specifications and status
4. Change status using dropdown (updates in real-time)
5. Schedule maintenance using dialog
6. Monitor maintenance overdue warnings

### Track Sensor Battery

1. Navigate to equipment list, Sensors tab
2. View battery indicators on cards
3. Click sensor with low battery warning
4. See detailed battery visualization
5. Check active alerts section
6. Monitor last communication timestamp

### Schedule Maintenance

1. From pump detail page, click "Schedule Maintenance"
2. Fill form: date, description, duration, notes
3. Submit form
4. View scheduled maintenance in Maintenance page
5. Track upcoming maintenance in schedule tab
6. View completed maintenance in history tab

### View Reports

1. Navigate to `/dashboard/equipment/reports`
2. View summary cards for quick insights
3. Analyze equipment distribution pie chart
4. Check status distribution bar chart
5. Monitor maintenance trends over time
6. Review sensor battery status distribution
7. Export data using PDF/CSV buttons

## ⚙️ Configuration

### API Endpoints

Equipment hooks use these endpoints:
- `GET /api/pumps` - List pumps
- `GET /api/pumps/:id` - Get pump details
- `PUT /api/pumps/:id` - Update pump status
- `POST /api/pumps/:id/maintenance` - Schedule maintenance
- `GET /api/sensors` - List sensors
- `GET /api/sensors/:id` - Get sensor details
- `GET /api/maintenance` - Get maintenance schedule
- `GET /api/equipment/statistics` - Get statistics

### WebSocket

Connect to: `ws://localhost:8080/ws-events`

STOMP topics:
- `/topic/equipment` - Equipment events
- `/topic/maintenance` - Maintenance events

## 🧪 Testing Real-time Updates

1. Start backend services (Eureka, Gateway, Equipment, Supervision)
2. Start frontend: `npm run dev`
3. Navigate to Equipment page
4. Observe "Live" badge in top right
5. Create/update equipment via API or backend
6. Watch UI automatically update without refresh

## 📱 Responsive Design

All pages are fully responsive:
- **Mobile**: Stacked cards, single column
- **Tablet**: 2-column grid for cards
- **Desktop**: 3-column grid, sidebar layouts
- Charts adapt to container width
- Tables scroll horizontally on mobile

## 🎨 Design System

### Colors

- **Blue** (`#3b82f6`): Primary, Active status, Pumps
- **Green** (`#10b981`): Success, Good battery, Sensors
- **Yellow** (`#f59e0b`): Warning, Low battery, Maintenance
- **Red** (`#ef4444`): Error, Critical battery, Overdue
- **Gray** (`#6b7280`): Inactive, Offline

### Icons (lucide-react)

- `Activity` - Equipment/Sensors
- `Droplet` - Pumps
- `Battery*` - Battery status
- `Wrench` - Maintenance
- `BarChart3` - Reports
- `Wifi/WifiOff` - Connection status
- `MapPin` - Location
- `Calendar` - Dates

## 🔄 State Management

- **React Query**: Server state (equipment data, maintenance)
- **Zustand**: UI state (sidebar, filters)
- **React Hook Form**: Form state (maintenance form)
- **Local State**: Component-specific state (dialogs, tabs)

## 🛡️ Error Handling

All hooks include error handling with toast notifications:

```typescript
onError: (error: any) => {
  toast.error(error?.response?.data?.message || 'Failed to update pump');
}
```

Error states displayed in UI:
- Loading states with skeletons
- Error messages with retry buttons
- Empty states with helpful messages

## 📦 Dependencies

New dependencies added:
- `recharts` - Charts library (already installed)
- `sonner` - Toast notifications (already installed)
- `@radix-ui/react-progress` - Progress component
- `@radix-ui/react-tabs` - Tabs component

## 🔐 Authentication

All equipment pages require authentication via dashboard layout. Redirects to `/login` if not authenticated.

## 🚧 Future Enhancements

Potential improvements:
- Export maintenance records to PDF
- Filter equipment by farm
- Advanced search with multiple criteria
- Historical battery level charts
- Maintenance calendar view
- Push notifications for critical alerts
- Equipment comparison tool
- Bulk status updates

## 📝 Notes

- WebSocket connection automatically reconnects on disconnect
- All dates formatted using Intl.DateTimeFormat for localization
- Pagination uses 0-based indexing (API) converted to 1-based (UI)
- Status colors consistent across all components
- Loading states prevent layout shift
- All forms use Zod validation
- Toast notifications for all mutations

## 🎉 Summary

Complete equipment monitoring system with:
- ✅ 8 pages (list, 2 details, maintenance, reports, 3 loading)
- ✅ 3 reusable components (battery, status, form)
- ✅ Real-time WebSocket updates with auto-reconnect
- ✅ 4 interactive charts with Recharts
- ✅ Search and filtering
- ✅ Pagination
- ✅ Maintenance scheduling
- ✅ Status controls
- ✅ Battery monitoring
- ✅ Alert indicators
- ✅ Responsive design
- ✅ TypeScript types
- ✅ Error handling
- ✅ Loading states

**Total: 15 files created, 4 files updated**
**Commit**: `95d9ad7` - feat(frontend): implement equipment monitoring with real-time updates
**Pushed to GitHub**: ✅
