export default {
  async fetch(request) {
    const url = new URL(request.url)

    if (url.pathname === '/api/health') {
      return Response.json({ ok: true, service: '拾光校园失物招领' })
    }

    return new Response('Not Found', { status: 404 })
  },
}
