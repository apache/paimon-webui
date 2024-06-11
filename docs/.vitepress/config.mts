import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Paimon Web",
  description: "Web UI for Apache Paimon",
  themeConfig: {
    logo: "/public/favicon_blue.svg",
    head: [["link", { rel: "icon", href: "/public/favicon_blue.svg" }]],
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Docs', link: '/src/guide/getting-started' }
    ],

    sidebar: [
      {
        text: 'Get Started',
        items: [
          { text: 'What is Paimon Web', link: '/src/guide/about' },
          { text: 'Quick Start', link: '/src/guide/getting-started' }
        ]
      }
    ],

    algolia: {
      appId: "R2IYF7ETH7",
      apiKey: "599cec31baffa4868cae4e79f180729b",
      indexName: "index",
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/apache/paimon-webui' }
    ]
  }
})
