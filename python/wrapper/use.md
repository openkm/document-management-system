# PyOKM
Python wrapper of the OpenKM RESTful API.


## Use 
To use the package, you can import it with:

```python
from okmDocument import OkmDocument
```

Now, you only have to create a new OkmDocument and call the methods that you need.

```python
mDocumentAPI = OkmDocument('username', 'password', 'http://<localhost>:8080/OpenKM')

mDocumentAPI.getDocument('<document-route>', '<destination-name>')
mDocumentAPI.deleteDocument('<document-route>')
```
