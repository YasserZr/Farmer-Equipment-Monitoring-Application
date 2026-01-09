export default function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[400px]">
      <h2 className="text-2xl font-bold mb-2">Farmer Not Found</h2>
      <p className="text-muted-foreground">The farmer you're looking for doesn't exist.</p>
    </div>
  );
}
