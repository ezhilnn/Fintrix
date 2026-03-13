// ================================================================
// ProtectedRoute.tsx
// Guards all authenticated routes.
// Reads authStore — no direct JWT parsing here.
// ================================================================

import { Navigate, useLocation } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { ROUTES }   from '../../utils/constants';
import './ProtectedRoute.css';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const { user, isAuthenticated, isLoading } = useAuthStore();
  const location = useLocation();

  // initAuth() still running — show spinner
  if (isLoading) {
    return (
      <div className="protected-route__loader">
        <div className="protected-route__spinner-wrap">
          <div className="protected-route__spinner-track" />
          <div className="protected-route__spinner-fill" />
        </div>
      </div>
    );
  }

  // Not logged in — send to /login, remember destination
  if (!isAuthenticated) {
    return (
      <Navigate
        to={ROUTES.LOGIN}
        state={{ from: location }}
        replace
      />
    );
  }

  // Logged in but profile not complete — force /profile setup
  // Skip redirect if already ON /profile to avoid infinite loop
  if (
    user &&
    !user.isProfileComplete &&
    location.pathname !== ROUTES.USER_PROFILE
  ) {
    return <Navigate to={ROUTES.USER_PROFILE} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;