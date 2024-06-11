---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "Paimon Web"
  tagline: Web UI for Apache Paimon
  actions:
    - theme: brand
      text: Get Started
      link: /src/guide/getting-started
    - theme: alt
      text: GitHub
      link: https://github.com/apache/paimon-webui

features:
  - title: Paimon table management
    details: Visual management of paimon tables, such as creating and modifying tables, adding columns, moving column order, etc. makes paimon table management simple.
  - title: Online SQL IDE
    details: Online SQL IDE can submit and execute Flink SQL tasks and display the result data.
  - title: CDC task drag integration
    details: CDC integration can create CDC integration tasks by dragging and dropping and submitting tasks to the Flink cluster.
  - title: Permission management
    details: Permission management provides button-level permission control, making the operation of paimon tables safer.
---
