import io
import logging
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from rembg import remove
from PIL import Image

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("ai-service")

app = FastAPI(title="GlowUp AI Closet scanning Microservice")

# Enable CORS for local cross-origin calls if needed
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def extract_dominant_color(img: Image.Image) -> str:
    """
    Extracts the dominant color from non-transparent pixels by downscaling
    and bucket-counting colors.
    """
    # Ensure image is in RGBA
    img = img.convert("RGBA")
    # Downscale to speed up processing and group similar pixels
    img = img.resize((32, 32), Image.Resampling.LANCZOS)
    
    pixels = list(img.getdata())
    # Only consider pixels that are not transparent
    non_transparent_colors = [p[:3] for p in pixels if p[3] > 128]
    
    if not non_transparent_colors:
        return "#FFFFFF"
    
    # Bucket colors to group similar shades together (round R, G, B to nearest 16)
    buckets = {}
    for c in non_transparent_colors:
        bucket = (
            min(255, max(0, round(c[0] / 16) * 16)),
            min(255, max(0, round(c[1] / 16) * 16)),
            min(255, max(0, round(c[2] / 16) * 16))
        )
        buckets[bucket] = buckets.get(bucket, 0) + 1
        
    if not buckets:
        return "#FFFFFF"
        
    # Find the most frequent color bucket
    dominant_rgb = max(buckets, key=buckets.get)
    return f"#{dominant_rgb[0]:02x}{dominant_rgb[1]:02x}{dominant_rgb[2]:02x}"

@app.get("/")
def health_check():
    return {"status": "ok", "service": "GlowUp AI Scan Service"}

@app.post("/remove-bg")
async def remove_background(file: UploadFile = File(...)):
    """
    Processes the uploaded image:
    1. Removes the background.
    2. Analyzes the processed image to find the primary color.
    3. Returns the PNG bytes with 'X-Primary-Color' custom header.
    """
    logger.info(f"Received background removal request for file: {file.filename}")
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Uploaded file must be an image.")
        
    try:
        # Read file bytes
        contents = await file.read()
        
        # Load image with Pillow
        input_image = Image.open(io.BytesIO(contents))
        
        # Process image using rembg
        output_image = remove(input_image)
        
        # Extract dominant color (ignoring transparency)
        primary_color = extract_dominant_color(output_image)
        logger.info(f"Extracted dominant color: {primary_color}")
        
        # Save output image to PNG byte stream
        img_byte_arr = io.BytesIO()
        output_image.save(img_byte_arr, format='PNG')
        img_byte_arr.seek(0)
        
        headers = {
            "Access-Control-Expose-Headers": "X-Primary-Color",
            "X-Primary-Color": primary_color
        }
        
        return StreamingResponse(
            img_byte_arr, 
            media_type="image/png", 
            headers=headers
        )
    except Exception as e:
        logger.error(f"Error processing image: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Image processing failed: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
