export function getUserName(): string {
  const match = document.cookie.match(new RegExp('(^| )username=([^;]+)'));
  if (match) return match[2];
  return 'test';
}
