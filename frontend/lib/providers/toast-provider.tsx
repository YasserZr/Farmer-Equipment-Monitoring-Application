'use client';

import { Toaster } from 'sonner';

/**
 * Toast Provider using Sonner
 */

export function ToastProvider() {
  return (
    <Toaster
      position="top-right"
      richColors
      closeButton
      toastOptions={{
        duration: 4000,
      }}
    />
  );
}
