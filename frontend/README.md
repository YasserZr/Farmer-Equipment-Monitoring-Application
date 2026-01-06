# Farmer Equipment Monitoring - Frontend

Modern Next.js 14 application for monitoring and managing agricultural equipment in real-time.

## 🚀 Features

- **Real-time Monitoring**: WebSocket integration for live equipment status updates
- **Farmer Management**: Complete CRUD operations for farmer accounts
- **Equipment Tracking**: Monitor pumps and sensors with detailed status information
- **Event Management**: Real-time alerts and event acknowledgment system
- **Dashboard Analytics**: Visual statistics and reporting
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Type Safety**: Full TypeScript implementation with strict type checking

## 📦 Tech Stack

- **Framework**: Next.js 14.2.3 (App Router)
- **Language**: TypeScript 5.4.3
- **Styling**: Tailwind CSS 3.4.3
- **State Management**: Zustand 4.5.2
- **Data Fetching**: React Query 5.28.4
- **HTTP Client**: Axios 1.6.8
- **Forms**: React Hook Form 7.51.2 + Zod 3.22.4
- **Icons**: Lucide React 0.365.0
- **Charts**: Recharts 2.12.3
- **Notifications**: Sonner 1.4.41

## 🏗️ Project Structure

```
frontend/
├── app/                      # Next.js App Router
│   ├── dashboard/           # Protected dashboard routes
│   │   ├── events/         # Events management
│   │   ├── farmers/        # Farmers management
│   │   ├── pumps/          # Pumps monitoring
│   │   ├── sensors/        # Sensors monitoring
│   │   ├── layout.tsx      # Dashboard layout with sidebar
│   │   └── page.tsx        # Dashboard home
│   ├── login/              # Authentication pages
│   ├── register/
│   ├── globals.css         # Global styles
│   ├── layout.tsx          # Root layout
│   └── page.tsx            # Landing page
├── components/             # Reusable UI components
├── hooks/                  # Custom React hooks
│   ├── use-auth.ts        # Authentication hook
│   ├── use-equipment.ts   # Equipment API hooks
│   ├── use-events.ts      # Events API hooks
│   └── use-farmers.ts     # Farmers API hooks
├── lib/                    # Utilities and configurations
│   ├── api-client.ts      # Axios instance with interceptors
│   ├── utils.ts           # Helper functions
│   ├── providers/         # React providers
│   │   ├── query-provider.tsx
│   │   └── toast-provider.tsx
│   └── store/             # Zustand stores
│       ├── auth-store.ts
│       └── ui-store.ts
├── types/                  # TypeScript type definitions
│   ├── common.ts
│   ├── equipment.ts
│   ├── event.ts
│   └── farmer.ts
├── .env.local.example     # Environment variables template
├── next.config.mjs        # Next.js configuration
├── tailwind.config.ts     # Tailwind CSS configuration
└── tsconfig.json          # TypeScript configuration
```

## 🔧 Setup & Installation

### Prerequisites

- Node.js 18+ and npm/yarn
- Backend services running (API Gateway on port 8080)

### Installation Steps

1. **Clone the repository**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment variables**
   ```bash
   cp .env.local.example .env.local
   ```

   Edit `.env.local` and set:
   ```env
   NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
   NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws-events
   ```

4. **Run development server**
   ```bash
   npm run dev
   ```

5. **Open browser**
   Navigate to [http://localhost:3000](http://localhost:3000)

## 🔑 API Integration

The frontend connects to the backend API Gateway (default: `http://localhost:8080`) with the following endpoints:

- **Farmers Service**: `/api/farmers/**`
- **Equipment Service**: `/api/equipment/**`, `/api/pumps/**`, `/api/sensors/**`
- **Supervision Service**: `/api/events/**`, `/api/statistics/**`
- **WebSocket**: `/ws-events/**` (STOMP over WebSocket)

### Authentication Flow

1. Login at `/login` page
2. API client automatically attaches JWT token to requests
3. Token stored in localStorage
4. Auto-redirect to login on 401 Unauthorized

### API Client Features

- **Automatic Token Injection**: Bearer token added to all requests
- **Error Handling**: Global error interceptor with user notifications
- **Request Logging**: Development mode logging
- **Retry Logic**: Automatic retry on network failures

## 📊 Available Scripts

```bash
# Development
npm run dev          # Start development server (port 3000)

# Build
npm run build        # Create production build
npm run start        # Start production server

# Code Quality
npm run lint         # Run ESLint
npm run type-check   # TypeScript type checking
```

## 🎨 Theming

Custom color palette defined in `tailwind.config.ts`:

- **Primary (Green)**: Equipment active states, success messages
- **Secondary (Red)**: Critical alerts, errors
- **Neutral**: Text, borders, backgrounds

## 🔐 Authentication

The application uses JWT-based authentication:

1. User logs in with email/password
2. Backend returns JWT token
3. Token stored in localStorage
4. Token sent with every API request via Axios interceptor
5. Auto-logout on token expiration (401 response)

## 📱 Responsive Design

- **Mobile**: < 768px - Collapsible sidebar
- **Tablet**: 768px - 1024px - Optimized layouts
- **Desktop**: > 1024px - Full sidebar navigation

## 🔄 Real-time Updates

WebSocket connection for real-time event streaming:

- Connects to `/ws-events` endpoint
- Subscribes to `/topic/events` and `/topic/alerts`
- Automatic reconnection on disconnect
- Updates React Query cache on new events

## 📈 State Management

### Zustand Stores

- **Auth Store**: User session, login/logout
- **UI Store**: Sidebar state, modals

### React Query

- **Server State**: API data caching with automatic invalidation
- **Background Refetch**: Keep data fresh automatically
- **Optimistic Updates**: Immediate UI feedback

## 🧪 Type Safety

All API responses are typed with TypeScript interfaces matching backend DTOs:

- `Farmer`, `Farm`: Farmer service entities
- `ConnectedPump`, `ConnectedSensor`: Equipment entities
- `EquipmentEvent`: Supervision event entity
- `PaginatedResponse<T>`: Spring Data Page wrapper

## 🚧 Future Enhancements

- [ ] Add WebSocket real-time event streaming
- [ ] Implement charts with Recharts
- [ ] Add data export (CSV, PDF)
- [ ] Multi-language support (i18n)
- [ ] Dark mode theme
- [ ] Progressive Web App (PWA)
- [ ] Offline support with service workers

## 📝 License

MIT License - See LICENSE file for details

## 👥 Support

For issues and questions, please create an issue in the repository.
