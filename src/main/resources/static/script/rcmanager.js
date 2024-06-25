/**
 * Resource manager for lecture, test etc. Upload/remove images. 
 */

class ResourceManager {


    constructor(containerId, uploadService, params) {
        this.containerId = containerId;
        this.uploadServiceURL = uploadService;

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
            cellAbort.appendChild(this.btnAbort);

            var outputRow = this.table.insertRow();
            var cellOutput = outputRow.insertCell();
            cellOutput.colSpan = 3;
            this.log = document.createElement('span');
            cellProgress.appendChild(this.progressBar);
            cellOutput.appendChild(this.log);

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
                        alert(xhr.responseText);
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
                alert(params['lectureId']);
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
    }
}