# Image Processor

An image processing application.

## How to use:

Clone the repo, change to the project directory, and run with gradlew

```./gradlew run```

## Features:

- read and write image from/to local storage drive.
- generate noise depth map.
- filters including Gaussian blur, gray scale, sobel edges, and canny edges.
- per pixel binary operations with two images called mixers.
- cache an image for later use.
- undo the last operation (excluding write to local drive).
