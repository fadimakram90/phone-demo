import logo from './logo.svg';
import './App.css';
import { useEffect, useState } from 'react';
import axios from 'axios';
import DataTable from 'react-data-table-component';
import ReactCountryFlag from 'react-country-flag';
import countryMap, { countryOptions, statusOptions } from './CountryCodeMapper';
import Select from 'react-select';

function App() {
  const [data, setData] = useState([]);
	const [loading, setLoading] = useState(false);
  const [totalRows, setTotalRows] = useState(0);
  // const [page, setPage] = useState(0);
  const [searchCountries, setSearchCountries] = useState([]);
  const [searchStatus, setSearchStatus] = useState([]);
  
  const fetchCustomers = async (page, sCountries, sStatus) => {
		setLoading(true);
    const searchString = prepareSearchString(sCountries, sStatus);
		const response = await axios.get(`http://localhost/api/customer?pageNum=${page}&pageSize=25${searchString}`);

		setData(response.data);
    setTotalRows(response.headers.totalitems);
		setLoading(false);
  };

  const prepareSearchString = (sCountries, sStatus) => {
    let searchString = "";
    if (sCountries) {
      searchString += sCountries.length > 0 ? ('&country=' + sCountries.join()) : '';
    } else if (searchCountries.length > 0) {
      searchString += ('&country=' + searchCountries.join());
    }
    
    if (sStatus) {
      searchString += sStatus.length > 0 ? ('&phoneValidation=' + sStatus.join()) : '';
    } else if (searchStatus.length > 0) {
      searchString += ('&phoneValidation=' + searchStatus.join());
    }
    return searchString;
  }

  const handleCountriesChange = value => {
    let newSCountries = value.map(item => {return item['value']});
    setSearchCountries(newSCountries);
    console.log(searchCountries);
    fetchCustomers(0, newSCountries)
  }

  const handleStatusChange = value => {
    let newSStatus = value.map(item => {return item['value']});
    setSearchStatus(newSStatus);
    fetchCustomers(0, undefined, newSStatus)
  }
  
  const handlePageChange = page => {
		fetchCustomers(page - 1);
  };

  const getCode = country => {
    return countryMap.get(country);
  }
  
  const columns = [
    {
      name: 'Name',
      selector: row => row.name,
    },
    {
      name: 'Phone',
      selector: row => row.phone,
    },
    {
      name: 'Country',
      selector: row => <div><ReactCountryFlag countryCode={getCode(row.country)} svg /> {row.country}</div>,
    },
    {
      name: 'Code',
      selector: row => "+" + row.code,
    },
    {
      name: 'Phone Status',
      selector: row => row.phoneValidity,
    }
];

  useEffect(() => {
		fetchCustomers(0); 
	}, []);

  return (
    <div className="App" >
      <table style={{width: "100%"}}>
        <tr>
          <td style={{width: "50%"}}>
            <Select options={countryOptions} isMulti onChange={handleCountriesChange} placeholder="Countries"/>
          </td>
        <td style={{width: "50%"}}>
          <Select options={statusOptions} isMulti onChange={handleStatusChange} placeholder="Phone Status"/>
        </td>
        </tr>
      </table>
      <DataTable
        columns={columns}
        data={data}
        progressPending={loading}
        pagination
        paginationServer
        paginationTotalRows={totalRows}
        paginationPerPage={25}
        paginationComponentOptions={{noRowsPerPage: true}}
        onChangePage={handlePageChange}
	  	/>
    </div>
  );
}

export default App;
