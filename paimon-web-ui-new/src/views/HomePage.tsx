import { defineComponent } from 'vue';

export default defineComponent({
  name: 'HomePage',
  setup() {
    const handleRequest = () => {
      console.log('测试成功')
    }

    return { handleRequest }
  },
  render() {
    return (
      <div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-sm">
          <img class="mx-auto h-10 w-auto" src="https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=600" alt="Your Company" />
          <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">Welcome to Paimon</h2>
        </div>
        <div class="flex justify-center py-12">
          <n-button onClick={this.handleRequest}>请求测试</n-button>
        </div>
      </div>
    );
  },
});
