# paimon-web-ui-new

This UI starts from Vue 3 in Vite.

## IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur) + [TypeScript Vue Plugin (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.vscode-typescript-vue-plugin).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [TypeScript Vue Plugin (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.vscode-typescript-vue-plugin) to make the TypeScript language service aware of `.vue` types.

If the standalone TypeScript plugin doesn't feel fast enough to you, Volar has also implemented a [Take Over Mode](https://github.com/johnsoncodehk/volar/discussions/471#discussioncomment-1361669) that is more performant. You can enable it by the following steps:

1. Disables the built-in TypeScript extension.
    1) Runs `Extensions: Show Built-in Extensions` from VSCode's command palette.
    2) Finds `TypeScript and JavaScript Language Features`, right click and select `Disable (Workspace)`.
2. Reloads the VSCode window by running `Developer: Reload Window` from the command palette.

## Customize configuration

See [Vite Configuration Reference](https://vitejs.dev/config/).

## Project Setup

```sh
pnpm install
```

### Compile and Hot-Reload for Development

```sh
pnpm dev
```

- If you can't a backend server. You can running project with mock mode

    This will open a mock server service and api document.

    The mock server will in [http://localhost:10088](http://localhost:10088)

    The mock api document will in [http://localhost:10090](http://localhost:10090)

```sh
pnpm dev:mock & pnpm run mock
```



### Type-Check, Compile and Minify for Production

```sh
pnpm build
```

### Lint with [ESLint](https://eslint.org/)

```sh
pnpm lint
```

# License

[Apache 2.0 License.](/LICENSE)
