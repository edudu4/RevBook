import { useEffect } from 'react';
import { useLocation } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { BookOpen } from 'lucide-react';
import { API_BASE_URL, type LoginApiResponse } from '@/lib/api';
import { paraUsuario } from '@/lib/mapeadores';

export default function Login() {
  const [, setLocation] = useLocation();
  const { login } = useAuth();

  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);

    script.onload = () => {
      if ((window as any).google) {
        (window as any).google.accounts.id.initialize({
          client_id: 'YOUR_GOOGLE_CLIENT_ID',
          callback: handleCredentialResponse,
        });
        (window as any).google.accounts.id.renderButton(
          document.getElementById('google-button'),
          { theme: 'outline', size: 'large' }
        );
      }
    };
  }, []);

  const handleCredentialResponse = async (response: any) => {
    try {
      const result = await fetch(`${API_BASE_URL}/auth/google`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: response.credential }),
      });

      if (result.ok) {
        const data: LoginApiResponse = await result.json();
        login(data.access_token, paraUsuario(data.user));
        setLocation('/');
      }
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="w-full max-w-md px-8 py-12 bg-card rounded-lg shadow-lg border border-border">
        <div className="flex flex-col items-center mb-8">
          <BookOpen className="w-12 h-12 text-accent mb-4" />
          <h1 className="text-3xl font-bold text-foreground mb-2">RevBook</h1>
          <p className="text-muted-foreground text-center">Compartilhe suas resenhas favoritas</p>
        </div>

        <div className="space-y-6">
          <div id="google-button" className="flex justify-center"></div>
          
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-border"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-card text-muted-foreground">ou continue sem conta</span>
            </div>
          </div>

          <button
            onClick={() => setLocation('/')}
            className="w-full px-4 py-2 bg-muted text-foreground rounded-md hover:bg-muted/80 transition-colors"
          >
            Explorar como visitante
          </button>
        </div>

        <p className="text-center text-sm text-muted-foreground mt-8">
          Faça login para adicionar e avaliar resenhas
        </p>
      </div>
    </div>
  );
}
