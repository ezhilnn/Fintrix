// ================================================================
// AppLayout.tsx
// Wraps all protected pages with Sidebar + mobile Topbar.
//
// Usage in AppRouter.tsx — replace bare ProtectedRoute children:
//   <ProtectedRoute>
//     <AppLayout>
//       <DashboardPage />
//     </AppLayout>
//   </ProtectedRoute>
//
// Or use the convenience wrapper <ProtectedLayout> below which
// combines ProtectedRoute + AppLayout in one component.
// ================================================================

import { useState } from 'react';
import Sidebar from './Sidebar';
import Topbar from './Topbar ';
import './AppLayout.css';

interface AppLayoutProps {
  children: React.ReactNode;
}

const AppLayout = ({ children }: AppLayoutProps) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="app-layout">

      {/* Fixed left sidebar (desktop always visible, mobile slides in) */}
      <Sidebar
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      {/* Mobile top bar — hidden on desktop */}
      <Topbar onMenuClick={() => setSidebarOpen(true)} />

      {/* Page content */}
      <main className="app-layout__main">
        {children}
      </main>

    </div>
  );
};

export default AppLayout;