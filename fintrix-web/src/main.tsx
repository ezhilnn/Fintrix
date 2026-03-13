// ================================================================
// main.tsx — application entry point
//
// CSS import order:
//   1. variables.css  — design tokens (consumed by all component CSS)
//   2. global.css     — reset + reusable atoms (.btn, .form-input…)
//   Component CSS files are imported inside each component file.
// ================================================================

import { StrictMode } from 'react';
import { createRoot }  from 'react-dom/client';

import './styles/variables.css';
import './styles/global.css';

import AppRouter from './router/AppRouter';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppRouter />
  </StrictMode>,
);