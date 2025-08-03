import React, { useState } from 'react';
import NewsForm from './components/NewsForm';
import ChartComponent from './components/ChartComponent';

function App() {
  const [currentPage, setCurrentPage] = useState('form');

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial', maxWidth: '1000px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
    
        <div>
          <h1>COVID DATA PROJECT</h1>
        </div>

        <img 
          src="/logo.png" 
          alt="Logo" 
          style={{ width: '300px', height: '100px' }}
        />
      </div>
      
      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={function() { setCurrentPage('form'); }}
          style={{
            padding: '15px 25px',
            backgroundColor: currentPage === 'form' ? 'green' : 'lightgray',
            color: currentPage === 'form' ? 'white' : 'black',
            border: 'none',
            marginRight: '10px',
            cursor: 'pointer'
          }}
        >
          Add New Data
        </button>
        
        <button 
          onClick={function() { setCurrentPage('chart'); }}
          style={{
            padding: '15px 25px',
            backgroundColor: currentPage === 'chart' ? 'blue' : 'lightgray',
            color: currentPage === 'chart' ? 'white' : 'black',
            border: 'none',
            cursor: 'pointer'
          }}
        >
          View Reports
        </button>
      </div>
      
      {currentPage === 'form' && <NewsForm />}
      {currentPage === 'chart' && <ChartComponent />}
    </div>
  );
}

export default App;