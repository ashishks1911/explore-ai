import axios from 'axios'
import { useRef, useState } from 'react'
import Loader from './components/Loader';
function App() {
  const [response, setResponse] = useState('');
  const [isFetching, setIsFetching] = useState(false);
  const ref = useRef();

  const getResponse = async () => {
    const prompt = ref.current.value;
    setResponse('');
    console.log(prompt)
    try {

      setIsFetching(true);
      const response = await axios.get(`http://localhost:8080/ask-ai?prompt=${prompt}`)
      setResponse(response.data);
      console.log(response);
      setIsFetching(false)
    }
    catch (error) {
      setIsFetching(false)
      console.log(error)
    }

  }

  const getLocation = async () => {
    
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition( async (postion) => {
        const lat = Number.parseFloat(postion.coords.latitude).toFixed(2);
        const lon = Number.parseFloat(postion.coords.longitude).toFixed(2);
        const response = await axios.get(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true`);
        console.log(response.data);
      })

    } else {
      alert("Geolocation is not supported by this browser.");
    }

  }

  return (
    <div className='flex flex-col items-center'>
      <div className='w-96 flex gap-2 bg-green-300 p-3'>
        <input type="text" className=' px-2 w-full py-2 border' ref={ref} />
        <button className='px-5 py-1 bg-blue-500 text-white rounded-md' onClick={getResponse}>Ask</button>

        <div>
          <button className='bg-red-400 text-white p-2' onClick={getLocation}>Location</button>
        </div>
      </div>
      <div className='mt-3 w-[70%] mx-4'>
        {
          isFetching && <Loader />
        }
        {response}
      </div>
    </div>
  )
}

export default App
