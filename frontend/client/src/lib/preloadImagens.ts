export function preloadImagens(urls: (string | undefined | null)[], timeoutMs = 4000): Promise<void> {
  const validas = Array.from(new Set(urls.filter((u): u is string => !!u)));
  if (validas.length === 0) {
    return Promise.resolve();
  }

  const promessas = validas.map(
    (url) =>
      new Promise<void>((resolve) => {
        const img = new Image();
        img.onload = () => resolve();
        img.onerror = () => resolve();
        img.src = url;
      })
  );

  const timeout = new Promise<void>((resolve) => setTimeout(resolve, timeoutMs));
  return Promise.race([Promise.all(promessas).then(() => undefined), timeout]);
}
