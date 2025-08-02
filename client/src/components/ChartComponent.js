import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import axios from 'axios';

function ChartComponent() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [chartType, setChartType] = useState('daily');
  const [selectedCity, setSelectedCity] = useState(''); 
  const [cities, setCities] = useState([]); 

  function getData() {
    setLoading(true);
    
    let url = 'http://localhost:8080/covid/api/chart/' + chartType;
    if (selectedCity !== '') {
      url = url + '?city=' + selectedCity;
    }
    
    axios.get(url)
      .then(function(response) {
        console.log('Response Data:', response.data);
        
        // Veriyi basit hale getir
        let chartData = [];
        for(let i = 0; i < response.data.length; i++) {
          let item = response.data[i];
          chartData.push({
            chartDate: item.date,
            chartCase: chartType === 'daily' ? item.dailyCases : item.totalCases,
            chartDeath: chartType === 'daily' ? item.dailyDeaths : item.totalDeaths,
            chartDischarges: chartType === 'daily' ? item.dailyDischarges : item.totalDischarges
          });
        }
        
        setData(chartData);
        setLoading(false);
      })
      .catch(function(error) {
        console.log('Hata:', error);
        setLoading(false);
      });
  }

  // Şehir listesini getir
  function getCities() {
    axios.get('http://localhost:8080/covid/api/get/all')
      .then(function(response) {
        console.log('All Data:', response.data);
        
        // Veri olan şehirleri çıkar
        let citiesWithData = [];
        for(let i = 0; i < response.data.length; i++) {
          let city = response.data[i].city;
          if (city && citiesWithData.indexOf(city) === -1) {
            citiesWithData.push(city);
          }
        }
        
        // Alfabetik sırala
        citiesWithData.sort();
        setCities(citiesWithData);
        console.log('The available cities:', citiesWithData);
      })
      .catch(function(error) {
        console.log('Error:', error);
      });
  }


  useEffect(function() {
    getData();
    getCities(); 
  }, [chartType, selectedCity]); 

  return (
    <div style={{ 
      border: '2px solid blue', 
      padding: '20px', 
      marginTop: '20px'
    }}>
      <h2>Covid Report</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={function() { setChartType('daily'); }}
          style={{
            padding: '10px',
            backgroundColor: chartType === 'daily' ? 'blue' : 'gray',
            color: 'white',
            border: 'none',
            marginRight: '10px'
          }}
        >
          Daily
        </button>
        
        <button 
          onClick={function() { setChartType('cumulative'); }}
          style={{
            padding: '10px',
            backgroundColor: chartType === 'cumulative' ? 'blue' : 'gray',
            color: 'white',
            border: 'none',
            marginRight: '10px'
          }}
        >
          Cumulative
        </button>
        
        <select 
          value={selectedCity} 
          onChange={function(e) { setSelectedCity(e.target.value); }}
          style={{ padding: '10px', marginRight: '10px' }}
        >
          <option value="">Turkey</option>
          
          {/* Availabile Cities */}
          <optgroup label="📊 Cities with Data Available">
            {cities.map(function(city) {
              return <option key={city} value={city}>{city} ✓</option>;
            })}
          </optgroup>
          
          {/* All the cities */}
          <optgroup label="🏙️ All Cities">
            <option value="Adana">Adana</option>
            <option value="Adıyaman">Adıyaman</option>
            <option value="Afyonkarahisar">Afyonkarahisar</option>
            <option value="Ağrı">Ağrı</option>
            <option value="Aksaray">Aksaray</option>
            <option value="Amasya">Amasya</option>
            <option value="Ankara">Ankara</option>
            <option value="Antalya">Antalya</option>
            <option value="Ardahan">Ardahan</option>
            <option value="Artvin">Artvin</option>
            <option value="Aydın">Aydın</option>
            <option value="Balıkesir">Balıkesir</option>
            <option value="Bartın">Bartın</option>
            <option value="Batman">Batman</option>
            <option value="Bayburt">Bayburt</option>
            <option value="Bilecik">Bilecik</option>
            <option value="Bingöl">Bingöl</option>
            <option value="Bitlis">Bitlis</option>
            <option value="Bolu">Bolu</option>
            <option value="Burdur">Burdur</option>
            <option value="Bursa">Bursa</option>
            <option value="Çanakkale">Çanakkale</option>
            <option value="Çankırı">Çankırı</option>
            <option value="Çorum">Çorum</option>
            <option value="Denizli">Denizli</option>
            <option value="Diyarbakır">Diyarbakır</option>
            <option value="Düzce">Düzce</option>
            <option value="Edirne">Edirne</option>
            <option value="Elazığ">Elazığ</option>
            <option value="Erzincan">Erzincan</option>
            <option value="Erzurum">Erzurum</option>
            <option value="Eskişehir">Eskişehir</option>
            <option value="Gaziantep">Gaziantep</option>
            <option value="Giresun">Giresun</option>
            <option value="Gümüşhane">Gümüşhane</option>
            <option value="Hakkari">Hakkari</option>
            <option value="Hatay">Hatay</option>
            <option value="Iğdır">Iğdır</option>
            <option value="Isparta">Isparta</option>
            <option value="İstanbul">İstanbul</option>
            <option value="İzmir">İzmir</option>
            <option value="Kahramanmaraş">Kahramanmaraş</option>
            <option value="Karabük">Karabük</option>
            <option value="Karaman">Karaman</option>
            <option value="Kars">Kars</option>
            <option value="Kastamonu">Kastamonu</option>
            <option value="Kayseri">Kayseri</option>
            <option value="Kırıkkale">Kırıkkale</option>
            <option value="Kırklareli">Kırklareli</option>
            <option value="Kırşehir">Kırşehir</option>
            <option value="Kilis">Kilis</option>
            <option value="Kocaeli">Kocaeli</option>
            <option value="Konya">Konya</option>
            <option value="Kütahya">Kütahya</option>
            <option value="Malatya">Malatya</option>
            <option value="Manisa">Manisa</option>
            <option value="Mardin">Mardin</option>
            <option value="Mersin">Mersin</option>
            <option value="Muğla">Muğla</option>
            <option value="Muş">Muş</option>
            <option value="Nevşehir">Nevşehir</option>
            <option value="Niğde">Niğde</option>
            <option value="Ordu">Ordu</option>
            <option value="Osmaniye">Osmaniye</option>
            <option value="Rize">Rize</option>
            <option value="Sakarya">Sakarya</option>
            <option value="Samsun">Samsun</option>
            <option value="Siirt">Siirt</option>
            <option value="Sinop">Sinop</option>
            <option value="Sivas">Sivas</option>
            <option value="Şanlıurfa">Şanlıurfa</option>
            <option value="Şırnak">Şırnak</option>
            <option value="Tekirdağ">Tekirdağ</option>
            <option value="Tokat">Tokat</option>
            <option value="Trabzon">Trabzon</option>
            <option value="Tunceli">Tunceli</option>
            <option value="Uşak">Uşak</option>
            <option value="Van">Van</option>
            <option value="Yalova">Yalova</option>
            <option value="Yozgat">Yozgat</option>
            <option value="Zonguldak">Zonguldak</option>
          </optgroup>
        </select>
        
        <button onClick={getData} style={{
          padding: '10px',
          backgroundColor: 'green',
          color: 'white',
          border: 'none'
        }}>
          Refresh
        </button>
      </div>

      {loading && <p>Loading...</p>}

      {!loading && data.length > 0 && (
        <div>
          <h3>{chartType === 'daily' ? 'Daily Data' : 'Cumulative Data'}</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={data}>
              <XAxis dataKey="chartDate" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="chartCase" stroke="orange" name="Case" />
              <Line type="monotone" dataKey="chartDeath" stroke="red" name="Death" />
              <Line type="monotone" dataKey="chartDischarges" stroke="green" name="Discharges" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}

      {!loading && data.length === 0 && (
        <p>There is no data available for this city. Please add your news first!</p>
      )}
    </div>
  );
}

export default ChartComponent;