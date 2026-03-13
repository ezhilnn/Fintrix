// ================================================================
// App.tsx
// AppRouter owns BrowserRouter + all routes.
// This file exists only as the conventional React tree root
// in case you need to add global providers (e.g. Toaster) later.
// ================================================================

import AppRouter from './router/AppRouter';

const App = () => <AppRouter />;

export default App;