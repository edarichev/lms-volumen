/**
 * Resource manager for lecture, test etc. Upload/remove images. 
 */

class ResourceManager {


    constructor(containerId, params) {
        this.containerId = containerId;
        this.uploadServiceURL = params['uploadService'];
        this.deleteServiceURL = params['deleteService'];
        this.loadListServiceURL = params['loadListService'];
        
        /**
         * Добавляет только что загруженную картинку в список
         */
        this.addSingleImageData = function(jsonObject) {
            var cell = this.table.rows[2].cells[0];
            var div = document.createElement('div');
            div.style.maxWidth = '100px';
            div.style.maxHeight = '100px';
            div.style.margin = '5px';
            div.style.padding = '5px';
            div.style.border = '1px black dotted';
            div.style.textAlign = 'center';
            div.style.verticalAlign = 'middle';
            div.style.float = 'left';
            var img = document.createElement('img');
            img.src = jsonObject.uri;
            img.style.maxWidth = '90px';
            img.style.maxHeight = '90px';
            img.style.margin = '0px';
            div.appendChild(img);
            var br = document.createElement('br');
            div.appendChild(br);
            var a = document.createElement('a');
            a.innerText = 'Удалить';
            a.href = 'javascript:void(0);';
            a.imageId = jsonObject.id;
            a.rcManager = this;
            a.onclick = function() {
                if (!confirm('Удалить эту картинку?'))
                    return;
                // call delete image action
                const xhr = new XMLHttpRequest();
                xhr.imageContainer = this.parentNode; 
                xhr.rcManager = this.rcManager;
                xhr.onload = function() {
                    if (!(xhr.status >= 200 && xhr.status < 300)) { // OK 200 or 201 CREATED
                        this.rcManager.log.textContent = ("Error " + xhr.status + " " + xhr.statusText);
                    } else { // ok, simple remove parent from container
                        this.imageContainer.remove();
                        this.rcManager.log.textContent = 'Removed';
                    }
                }
                xhr.open('GET', this.rcManager.deleteServiceURL + "/" + this.imageId);
                xhr.send();
            }
            div.appendChild(a);
            cell.insertAdjacentElement('afterbegin', div);
        }
        
        this.loadImageList = function(jsonObjects) {
            var cell = this.table.rows[2].cells[0];
            cell.innerHTML = ""; // clear all
            for (var i = 0; i < jsonObjects.length; ++i) {
                var o = jsonObjects[i];
                this.addSingleImageData(o);
            }
        }
        
        this.beginLoadImageList = function() {
            const xhr = new XMLHttpRequest();
            xhr.rcManager = this;
            xhr.onload = function() {
                if (!(xhr.status >= 200 && xhr.status < 300)) { // OK 200 or 201 CREATED
                    this.rcManager.log.textContent = ("Error " + xhr.status + " " + xhr.statusText);
                } else { // ok, simple remove parent from container
                    this.rcManager.loadImageList(JSON.parse(xhr.responseText));
                }
            }
            xhr.open('GET', this.loadListServiceURL + "/" + Number.parseInt(params['lectureId']));
            xhr.send();
        }

        this.buildFrame = function() {
            // frame
            this.table = document.createElement('table');
            this.table.style.width = '100%';
            this.table.style.borderWidth = '1px';
            this.table.style.borderColor = 'black';
            this.table.style.borderStyle = 'solid';

            var container = document.getElementById(this.containerId);
            container.appendChild(this.table);
            var headerRow = this.table.insertRow();
            var cellUpload = headerRow.insertCell();
            cellUpload.style.width = '33%';
            cellUpload.style.textAlign = 'left';
            this.fileUpload = document.createElement('input');
            this.fileUpload.type = 'file';
            //this.fileUpload.multiple = true;
            cellUpload.appendChild(this.fileUpload);

            var cellProgress = headerRow.insertCell();
            cellProgress.style.width = '33%';
            this.progressBar = document.createElement('span');
            cellProgress.appendChild(this.progressBar);

            var cellAbort = headerRow.insertCell();
            cellAbort.style.width = '33%';
            cellAbort.style.textAlign = 'right';
            this.btnAbort = document.createElement('input');
            this.btnAbort.type = 'button';
            this.btnAbort.value = 'Abort';
            this.btnAbort.disabled = true;
            cellAbort.appendChild(this.btnAbort);

            var outputRow = this.table.insertRow();
            var cellOutput = outputRow.insertCell();
            cellOutput.colSpan = 3;
            this.log = document.createElement('span');
            cellProgress.appendChild(this.progressBar);
            cellOutput.appendChild(this.log);
            // for images
            var row = this.table.insertRow(2);
            var cell = row.insertCell();
            cell.colSpan = 3;


            this.fileUpload.rcManager = this;
            // source: https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequestUpload
            this.fileUpload.addEventListener("change", () => {
                const xhr = new XMLHttpRequest();
                xhr.timeout = 10000; // 10 seconds
                // Link abort button
                this.btnAbort.addEventListener(
                    "click",
                    () => {
                        xhr.abort();
                    },
                    { once: true },
                );

                // When the upload starts, we display the progress bar
                xhr.upload.addEventListener("loadstart", (event) => {
                    this.progressBar.classList.add("visible");
                    this.progressBar.value = 0;
                    this.progressBar.max = event.total;
                    this.log.textContent = "Uploading (0%)…";
                    this.btnAbort.disabled = false;
                });

                // Each time a progress event is received, we update the bar
                xhr.upload.addEventListener("progress", (event) => {
                    this.progressBar.value = event.loaded;
                    this.log.textContent = `Uploading (${(
                        (event.loaded / event.total) *
                        100
                    ).toFixed(2)}%)…`;
                });

                // When the upload is finished, we hide the progress bar.
                xhr.upload.addEventListener("loadend", (event) => {
                    this.progressBar.classList.remove("visible");
                    if (event.loaded !== 0) {
                        this.log.textContent = "Upload finished.";
                    }
                    this.btnAbort.disabled = true;
                    // to clear input field:
                    this.fileUpload.value = null;
                    this.fileUpload.type = "text";
                    this.fileUpload.type = "file";
                });

                // In case of an error, an abort, or a timeout, we hide the progress bar
                // Note that these events can be listened to on the xhr object too
                function errorAction(event) {
                    this.progressBar.classList.remove("visible");
                    this.log.textContent = `Upload failed: ${event.type}`;
                }
                xhr.upload.addEventListener("error", errorAction);
                xhr.upload.addEventListener("abort", errorAction);
                xhr.upload.addEventListener("timeout", errorAction);

                xhr.rcManager = this;
                this.xhrOnLoad = function() {
                    if (!(xhr.status >= 200 && xhr.status < 300)) { // OK 200 or 201 CREATED
                        this.rcManager.log.textContent = ("Error " + xhr.status + " " + xhr.statusText);
                    } else {//ok
                        this.rcManager.addSingleImageData(JSON.parse(xhr.responseText));
                    }
                }


                this.xhrOnError = function() {
                    this.rcManager.log.textContent = ('Connection error');
                };

                xhr.onload = this.xhrOnLoad;
                xhr.onerror = this.xhrOnError;

                // Build the payload
                const fileData = new FormData();
                fileData.append("file", this.fileUpload.files[0]);
                if (params !== undefined) {
                    if (params['lectureId'] !== undefined) {
                        fileData.append('lectureId', Number.parseInt(params['lectureId']));
                    } 
                }

                // Theoretically, event listeners could be set after the open() call
                // but browsers are buggy here
                xhr.open("POST", this.uploadServiceURL, true);

                // Note that the event listener must be set before sending (as it is a preflighted request)
                xhr.send(fileData);
            });
        }

        this.buildFrame();
        this.beginLoadImageList();
    }
}