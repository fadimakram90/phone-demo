const countryMap = new Map();

countryMap.set('Cameroon', 'CM');
countryMap.set('Ethiopia', 'ET');
countryMap.set('Morocco', 'MA');
countryMap.set('Mozambique', 'MZ');
countryMap.set('Uganda', 'UG');

export let countryOptions = [];
for (const [key, value] of countryMap.entries()) {
    countryOptions.push({label: key, value: key});
}

export let statusOptions = [ 
    {label: "INVALID", value: "INVALID"},
    {label: "VALID", value: "VALID"}
];

export default countryMap;