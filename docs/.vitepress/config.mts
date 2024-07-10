import { defineConfig } from 'vitepress'

export default defineConfig({
  title: "Paimon Web",
  description: "Web UI for Apache Paimon",

  themeConfig: {
    logo: "/favicon_blue.svg",
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Docs', link: '/src/guide/quick-start' }
    ],

    sidebar: [
      {
        text: 'Get Started',
        items: [
          { text: 'What is Paimon Web', link: '/src/guide/about' },
          { text: 'Quick Start', link: '/src/guide/quick-start' }
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
    ],

    footer: {
      copyright: 'Copyright © 2024 The Apache Software Foundation. Apache Paimon, Paimon, and its feather logo are trademarks of The Apache Software Foundation.'
    }
  }
})
