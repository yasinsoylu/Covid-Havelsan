import React, { useState } from 'react';
import axios from 'axios';

function NewsForm() {
  const [newsText, setNewsText] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  function handleSubmit(e) {
    e.preventDefault();
    
    if (newsText === '') {
      setError('The news area can not be empty!');
      return;
    }

    // Backend'e gönder
    axios.post('http://localhost:8080/covid/api/process', {
      inputText: newsText
    })
    .then(function(response) {
      console.log('Success:', response.data);
      setResult(response.data);
      setNewsText('');
      setError('');
    })
    .catch(function(error) {
      console.log('Error:', error);
      if (error.response && error.response.data && error.response.data.message) {
      setError(error.response.data.message);
    } else {
      setError("Bilinmeyen bir hata oluştu.");
    }
      setResult(null);
    });
  }

  return (
    <div style={{ 
      border: '2px solid green', 
      padding: '20px', 
      marginTop: '20px'
    }}>
      <h2>New data addition area:</h2>
      
      <form onSubmit={handleSubmit}>
        <textarea
          value={newsText}
          onChange={function(e) { setNewsText(e.target.value); }}
          placeholder="Enter your news in this area..."
          style={{
            width: '100%',
            height: '100px',
            marginBottom: '10px'
          }}
        />
        
        <button type="submit" style={{
          padding: '10px 20px',
          backgroundColor: 'green',
          color: 'white',
          border: 'none'
        }}>
          Submit
        </button>
      </form>

      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          {error}
        </div>
      )}

      {result && (
        <div style={{ 
          backgroundColor: 'lightgreen', 
          padding: '15px', 
          marginTop: '10px',
          borderRadius: '5px'
        }}>
          <h4>✅ Başarılı!</h4>
          <p>Tarih: {result.date}</p>
          <p>Şehir: {result.city}</p>
          <p>Vaka: {result.caseCount}</p>
          <p>Vefat: {result.deathCount}</p>
          <p>Taburcu: {result.dischargesCount}</p>
        </div>
      )}
    </div>
  );
}

export default NewsForm;